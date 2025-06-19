package com.example.howdoesthehumaneyeseethings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class Lesson1Fragment extends Fragment {

    private boolean isLightOn = false;
    private ImageView imageScene, imageSwitch, imageClassroom;
    private SeekBar brightnessSlider;
    private Bitmap originalBitmap, originalBackup;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Initialization failed!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);

        imageScene = view.findViewById(R.id.imageScene);
        imageSwitch = view.findViewById(R.id.imageSwitch);
        imageClassroom = view.findViewById(R.id.imageClassroom);
        brightnessSlider = view.findViewById(R.id.brightnessSlider);

        // Set initial image and backup original bitmap
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.classroom);
        originalBackup = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        imageClassroom.setImageBitmap(originalBackup);

        imageSwitch.setOnClickListener(v -> {
            isLightOn = !isLightOn;
            if (isLightOn) {
                imageScene.setImageResource(R.drawable.room_light);
                imageSwitch.setImageResource(R.drawable.switch_on);
            } else {
                imageScene.setImageResource(R.drawable.room_dark);
                imageSwitch.setImageResource(R.drawable.switch_off);
            }
        });

        brightnessSlider.setMax(100); // Range: 0 to 100
        brightnessSlider.setProgress(0); // Start from 0 (no brightness adjustment)

        brightnessSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    imageClassroom.setImageBitmap(originalBackup); // Restore original
                } else {
                    applyBrightnessWithOpenCV(progress);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }

    private void applyBrightnessWithOpenCV(float progressValue) {
        try {
            if (originalBackup == null) return;

            // Convert Bitmap to Mat
            Bitmap tempBitmap = originalBackup.copy(Bitmap.Config.ARGB_8888, true);
            Mat src = new Mat();
            Utils.bitmapToMat(tempBitmap, src);

            // Map progress (0–100) to brightness range (e.g., 0–50)
            float beta = (progressValue / 100f) * 200f;  // brightness offset
            float alpha = 1.0f; // keep contrast unchanged

            Mat result = new Mat();
            src.convertTo(result, -1, alpha, beta);  // use convertTo with alpha/brightness

            // Convert Mat back to Bitmap
            Bitmap resultBitmap = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(result, resultBitmap);

            imageClassroom.setImageBitmap(resultBitmap);

        } catch (Exception e) {
            Log.e("Lesson1", "Brightness adjustment failed: " + e.getMessage(), e);
        }
    }

}
