package com.example.howdoesthehumaneyeseethings;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class DragAndDropTestActivity extends AppCompatActivity {

    private Map<Integer, Integer> correctAnswers = new HashMap<>();
    private Map<Integer, FrameLayout> slotViews = new HashMap<>();

    private AudioHelper audioHelper;

    private ViewGroup answersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop_test);
        answersContainer = findViewById(R.id.answersContainer);

        audioHelper = new AudioHelper(this);

        ImageView dragImage = findViewById(R.id.draganddropimage);
        dragImage.setOnClickListener(v -> showZoomDialog(R.drawable.interactive_test));

        View.OnTouchListener touchListener = new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    return onDragStart(view); // Call the function you added earlier
                }
                return false;
            }
        };

        setupCorrectAnswers();
        setupDragAndDrop();

        ImageButton submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> checkAnswers());

        int[] answerIds = {
                R.id.answer1, R.id.answer2, R.id.answer3, R.id.answer4, R.id.answer5
        };
        for (int id : answerIds) {
            View view = findViewById(id);
            view.setOnTouchListener(touchListener);
        }

        ImageButton resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSlots(); // call your existing reset method
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        RelativeLayout layout = findViewById(R.id.dragDropRoot);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(0);
        animationDrawable.setExitFadeDuration(0);
        animationDrawable.start();
    }

    private void setupCorrectAnswers() {
        // slotId -> correct answer tag
        correctAnswers.put(R.id.slot1, 3);
        correctAnswers.put(R.id.slot2, 4);
        correctAnswers.put(R.id.slot3, 5);
        correctAnswers.put(R.id.slot4, 1);
        correctAnswers.put(R.id.slot5, 2);

        // Use FrameLayout instead of TextView
        slotViews.put(R.id.slot1, findViewById(R.id.slot1));
        slotViews.put(R.id.slot2, findViewById(R.id.slot2));
        slotViews.put(R.id.slot3, findViewById(R.id.slot3));
        slotViews.put(R.id.slot4, findViewById(R.id.slot4));
        slotViews.put(R.id.slot5, findViewById(R.id.slot5));
    }

    private void setupDragAndDrop() {
        View.OnDragListener dropListener = (v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DROP:
                    TextView draggedView = (TextView) event.getLocalState();
                    FrameLayout targetSlot = (FrameLayout) v;

                    // If slot already has an answer, reject drop
                    if (targetSlot.getChildCount() > 1) {
                        return false;
                    }

                    // Remove from previous parent first
                    ViewGroup currentParent = (ViewGroup) draggedView.getParent();
                    if (currentParent != null) {
                        currentParent.removeView(draggedView);
                    }

                    // Set layout params for consistency
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            150
                    );
                    params.bottomMargin = 4;
                    params.gravity = Gravity.BOTTOM;
                    draggedView.setLayoutParams(params);
                    draggedView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorOnPrimary));
                    draggedView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    draggedView.setTextSize(11);

                    targetSlot.addView(draggedView);
                    targetSlot.setTag(draggedView.getTag()); // Set tag for answer checking
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    if (!event.getResult()) { // If drop failed
                        TextView draggedViewEnd = (TextView) event.getLocalState();
                        ViewGroup originalParent = (ViewGroup) draggedViewEnd.getTag(R.id.original_parent);
                        if (originalParent != null) {
                            if (draggedViewEnd.getParent() != null) {
                                ((ViewGroup) draggedViewEnd.getParent()).removeView(draggedViewEnd);
                            }
                            originalParent.addView(draggedViewEnd);
                        }
                    }
                    v.invalidate();
                    return true;

            }
            return true;
        };

        for (int id : correctAnswers.keySet()) {
            findViewById(id).setOnDragListener(dropListener);
        }
    }

    private boolean onDragStart(View view) {
        view.setTag(R.id.original_parent, view.getParent());
        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
        ClipData dragData = new ClipData(
                (CharSequence) view.getTag(),
                new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                item
        );

        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);
        view.startDragAndDrop(dragData, myShadow, view, 0);
        return true;
    }


    private void checkAnswers() {
        boolean allCorrect = true;

        for (Map.Entry<Integer, Integer> entry : correctAnswers.entrySet()) {
            int slotId = entry.getKey();
            int correctTag = entry.getValue();

            FrameLayout slot = findViewById(slotId);
            Object tagObj = slot.getTag();

            if (tagObj != null && Integer.parseInt(tagObj.toString()) == correctTag) {
                slot.setBackgroundColor(Color.parseColor("#AAFFAA")); // light green
            } else {
                slot.setBackgroundColor(Color.parseColor("#FFAAAA")); // light red
                allCorrect = false;
            }
        }

        // Play sound based on result
        if (allCorrect) {
            audioHelper.playCorrect();
        } else {
            audioHelper.playWrong();
        }
    }


    private void resetSlots() {
        for (FrameLayout slot : slotViews.values()) {
            if (slot.getChildCount() > 1) {
                View answer = slot.getChildAt(1);
                slot.removeView(answer);
            }
            slot.setBackgroundColor(Color.parseColor("#DDDDDD"));
            slot.setTag(null);
        }

        // Reset answer boxes in the container
        resetAnswerBoxes();
    }

    private void resetAnswerBoxes() {
        // Clear current answers
        answersContainer.removeAllViews();

        // Inflate the answer boxes again from the XML layout
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.answers_layout, answersContainer, true);

        // Reattach the drag listener to each re-inflated answer view
        int[] answerIds = {
                R.id.answer1, R.id.answer2, R.id.answer3, R.id.answer4, R.id.answer5
        };
        for (int id : answerIds) {
            View answerView = answersContainer.findViewById(id);
            if (answerView != null) {
                answerView.setOnTouchListener((view, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        return onDragStart(view);
                    }
                    return false;
                });
            }
        }
    }

    public void onPrevClick(View view) {
        finish();
    }

    public void onAudioClick(View view) {
        audioHelper.showCustomVolumeDialog();
    }

    private void showZoomDialog(int imageResId) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_zoom_image, null);

        ZoomImageView zoomImageView = dialogView.findViewById(R.id.zoomImageView);
        zoomImageView.setImageResource(imageResId);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialog.show();
    }
}
