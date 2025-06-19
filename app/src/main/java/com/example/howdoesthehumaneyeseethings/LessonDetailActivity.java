package com.example.howdoesthehumaneyeseethings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LessonDetailActivity extends AppCompatActivity {
    private AudioHelper audioHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);
        audioHelper = new AudioHelper(this);

        TextView title = findViewById(R.id.detail_title);
        TextView description = findViewById(R.id.detail_description);
        ImageView image = findViewById(R.id.detail_image);

        // Get data from intent
        String lessonTitle = getIntent().getStringExtra("title");
        String lessonDescription = getIntent().getStringExtra("description");
        int imageResId = getIntent().getIntExtra("imageResId", -1);

        // Set data
        title.setText(lessonTitle);
        description.setText(lessonDescription);
        image.setImageResource(imageResId);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;

        switch (lessonTitle) {
            case "Lesson 1":
                fragment = new Lesson1Fragment();
                break;
            case "Lesson 2":
                fragment = new Lesson2Fragment();
                break;
            case "Lesson 3":
                fragment = new Lesson3Fragment();
                break;
            case "Lesson 4":
                fragment = new Lesson4Fragment();
                break;
        }

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.detail_custom_layout_container, fragment);
            transaction.commit();
        }

        ImageButton btnPrev = findViewById(R.id.btnPrev);
        ImageButton btnAudio = findViewById(R.id.btnAudio);

        btnPrev.setOnClickListener(v -> onPrevClick(v));
        btnAudio.setOnClickListener(v -> onAudioClick(v));
    }

    public void onPrevClick(View view) {
        finish();//close detail page
    }

    public void onAudioClick(View view) {
        audioHelper.showCustomVolumeDialog();
    }
}
