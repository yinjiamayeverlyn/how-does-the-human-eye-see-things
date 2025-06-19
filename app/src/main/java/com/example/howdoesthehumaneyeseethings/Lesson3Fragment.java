package com.example.howdoesthehumaneyeseethings;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;
public class Lesson3Fragment extends Fragment {

    private VideoView videoView;
    private MediaController mediaController;

    public Lesson3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment3, container, false);

        videoView = rootView.findViewById(R.id.videoView);

        rootView.findViewById(R.id.imagePart1).setOnClickListener(v -> showZoomDialog(R.drawable.part1));
        rootView.findViewById(R.id.imagePart2).setOnClickListener(v -> showZoomDialog(R.drawable.part2));
        rootView.findViewById(R.id.imagePart3).setOnClickListener(v -> showZoomDialog(R.drawable.part3));
        rootView.findViewById(R.id.imagePart4).setOnClickListener(v -> showZoomDialog(R.drawable.part4));
        rootView.findViewById(R.id.imagePart5).setOnClickListener(v -> showZoomDialog(R.drawable.part5));

        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + R.raw.eyework;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        mediaController = new MediaController(requireContext());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // Handler to auto-hide controller after delay
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable hideControlsRunnable = () -> {
            if (mediaController.isShowing()) {
                mediaController.hide();
            }
        };

        // Show controls on video tap and hide after delay
        videoView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!mediaController.isShowing()) {
                    mediaController.show();
                }
                // Reset auto-hide timer
                handler.removeCallbacks(hideControlsRunnable);
                handler.postDelayed(hideControlsRunnable, 3000); // Hide after 3 seconds
            }
            return false;
        });

        // Hide controller when touching outside the VideoView
        rootView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                int[] location = new int[2];
                videoView.getLocationOnScreen(location);
                float x = event.getRawX();
                float y = event.getRawY();
                int left = location[0];
                int top = location[1];
                int right = left + videoView.getWidth();
                int bottom = top + videoView.getHeight();

                boolean isOutsideVideo = !(x > left && x < right && y > top && y < bottom);
                if (isOutsideVideo && mediaController.isShowing()) {
                    mediaController.hide();
                }
            }
            return true; // consume touch
        });

        return rootView;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    private void showZoomDialog(int imageResId) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_zoom_image, null);

        ZoomImageView zoomImageView = dialogView.findViewById(R.id.zoomImageView);
        zoomImageView.setImageResource(imageResId);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        dialog.show();
    }
}
