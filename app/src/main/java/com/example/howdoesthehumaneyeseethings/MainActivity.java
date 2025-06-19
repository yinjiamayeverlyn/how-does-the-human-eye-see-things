package com.example.howdoesthehumaneyeseethings;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.ArCoreApk.Availability;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AudioHelper audioHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        audioHelper = new AudioHelper(this);

        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV init failed");
        } else {
            Log.i(TAG, "OpenCV loaded successfully");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RelativeLayout layout = findViewById(R.id.main_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(0);
        animationDrawable.setExitFadeDuration(0);
        animationDrawable.start();
    }

    public void onAudioClick(View view) {
        audioHelper.showCustomVolumeDialog();
    }

    public void onLearnClick(View view) {
        Intent intent = new Intent(this, LearningActivity.class);
        startActivity(intent);
    }

    public void onInteractiveTestClick(View view) {
        Intent intent = new Intent(this, DragAndDropTestActivity.class);
        startActivity(intent);
    }

    public void onQuizClick(View view) {
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }

    public void onExploreARClick(View view) {
        checkArAndStart();  // Safe AR support check
    }

    private void checkArAndStart() {
        Availability availability = ArCoreApk.getInstance().checkAvailability(this);

        if (availability.isSupported()) {
            Intent intent = new Intent(this, ARActivity.class);
            startActivity(intent);
        } else if (availability.isTransient()) {
            // Retry after a short delay if still checking support
            new Handler().postDelayed(this::checkArAndStart, 200);
        } else {
            // Device doesn't support AR → Show user-friendly alert
            new AlertDialog.Builder(this)
                    .setTitle("AR Not Supported")
                    .setMessage("Sorry, your device doesn't support AR features.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}
