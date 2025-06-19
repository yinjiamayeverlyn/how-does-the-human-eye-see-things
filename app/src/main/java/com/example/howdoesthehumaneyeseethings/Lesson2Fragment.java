package com.example.howdoesthehumaneyeseethings;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class Lesson2Fragment extends Fragment {

    private ImageView animationImageView;
    private ImageButton playButton;
    private AnimationDrawable animationDrawable;

    public Lesson2Fragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);

        // Initialize views
        animationImageView = view.findViewById(R.id.animationImageView);
        playButton = view.findViewById(R.id.playButton);

        // Set default static image (simulation1.png)
        animationImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.simulation1));

        // Set up play button click
        playButton.setOnClickListener(v -> {
            // Stop animation if it’s running
            if (animationDrawable != null && animationDrawable.isRunning()) {
                animationDrawable.stop();
            }

            // Set animation drawable (animation-list)
            animationImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.eye_light_animation));
            animationDrawable = (AnimationDrawable) animationImageView.getDrawable();

            animationDrawable.start();

            // Automatically reset to first frame after animation completes
            int totalDuration = getAnimationDuration(animationDrawable);
            new Handler().postDelayed(() -> {
                animationDrawable.stop();
                // Reset to simulation1.png after it finishes
                animationImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.simulation1));
            }, totalDuration);
        });

        return view;
    }

    // Helper method to calculate total animation duration
    private int getAnimationDuration(AnimationDrawable animation) {
        int duration = 0;
        for (int i = 0; i < animation.getNumberOfFrames(); i++) {
            duration += animation.getDuration(i);
        }
        return duration;
    }
}
