package com.example.howdoesthehumaneyeseethings;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import adapter.LessonAdapter;
import model.LessonItem;

public class LearningActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LessonAdapter adapter;
    private List<LessonItem> lessonList;
    private AudioHelper audioHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);
        audioHelper = new AudioHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Prepare data
        lessonList = new ArrayList<>();
        lessonList.add(new LessonItem("Lesson 1", "What is Light?", R.drawable.lesson1_image));
        lessonList.add(new LessonItem("Lesson 2", "How Does the Eye See Light?", R.drawable.lesson2_image));
        lessonList.add(new LessonItem("Lesson 3", "Parts of the Eye", R.drawable.lesson3_image));
        lessonList.add(new LessonItem("Lesson 4", "Optical Illusions", R.drawable.lesson4_image));

        adapter = new LessonAdapter(this, lessonList);
        recyclerView.setAdapter(adapter);

        ImageButton btnPrev = findViewById(R.id.btnPrev);
        ImageButton btnAudio = findViewById(R.id.btnAudio);

        btnPrev.setOnClickListener(v -> onPrevClick(v));
        btnAudio.setOnClickListener(v -> onAudioClick(v));
    }

    @Override
    protected void onResume() {
        super.onResume();
        FrameLayout layout = findViewById(R.id.learningbg);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(0);
        animationDrawable.setExitFadeDuration(0);
        animationDrawable.start();
    }

    public void onPrevClick(View view) {
        // Go back to MainActivity
        finish();
    }

    public void onAudioClick(View view) {
        audioHelper.showCustomVolumeDialog();
    }
}