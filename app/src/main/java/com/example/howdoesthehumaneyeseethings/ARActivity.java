package com.example.howdoesthehumaneyeseethings;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ARActivity extends AppCompatActivity {

    private ArFragment arFragment;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "OpenCV init failed");
        } else {
            Log.d("OpenCV", "OpenCV loaded successfully");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aractivity);

        // Check ARCore support
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            new AlertDialog.Builder(this)
                    .setTitle("AR Not Supported")
                    .setMessage("This device does not support AR features.")
                    .setPositiveButton("OK", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
            return;
        }

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        if (arFragment != null) {
            arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
                Session session = arFragment.getArSceneView().getSession();
                if (session != null) {
                    Config config = session.getConfig();
                    config.setLightEstimationMode(Config.LightEstimationMode.ENVIRONMENTAL_HDR);
                    session.configure(config);

                    Frame frame = arFragment.getArSceneView().getArFrame();
                    if (frame != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
                        try {
                            Image image = frame.acquireCameraImage();  // real YUV_420_888 image

                            // Convert YUV_420_888 to Bitmap
                            Bitmap bitmap = convertYUVToBitmap(image);

                            // Convert Bitmap to OpenCV Mat
                            Mat mat = new Mat();
                            Utils.bitmapToMat(bitmap, mat);
                            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2GRAY); // grayscale example

                            Log.d("OpenCV", "Frame processed: " + mat.cols() + "x" + mat.rows());

                            image.close();
                        } catch (Exception e) {
                            Log.e("OpenCV", "Image processing failed: " + e.getMessage());
                        }
                    }
                }
            });

            arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                if (plane.getType().name().equals("HORIZONTAL_UPWARD_FACING")) {
                    Anchor anchor = hitResult.createAnchor();
                    placeEyeModel(anchor);
                }
            });
        }
    }

    private void placeEyeModel(Anchor anchor) {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("human_eye/scene.glb"))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
                    node.setParent(anchorNode);
                    node.setRenderable(renderable);
                    node.select();
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Error loading model: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    // Utility: Convert YUV_420_888 Image to Bitmap
    public static Bitmap convertYUVToBitmap(Image image) {
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 90, out);
        byte[] jpegBytes = out.toByteArray();
        return android.graphics.BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);
    }
}
