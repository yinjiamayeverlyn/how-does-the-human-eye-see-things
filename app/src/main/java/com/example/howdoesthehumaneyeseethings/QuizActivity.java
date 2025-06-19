package com.example.howdoesthehumaneyeseethings;

import android.app.AlertDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class QuizActivity extends AppCompatActivity {

    private TextView questionText;
    private Button optionA, optionB, optionC;

    private int currentQuestion = 0;
    private int score = 0;
    private AudioHelper audioHelper;

    // Structure for questions
    private class Question {
        String question;
        String[] options;
        int correctIndex;
        String explanation;

        Question(String q, String[] o, int ci, String exp) {
            question = q;
            options = o;
            correctIndex = ci;
            explanation = exp;
        }
    }

    private Question[] questions = new Question[] {
            new Question("What do we need to see something?",
                    new String[]{"Air", "Water", "Light"}, 2,
                    "We need light to see things, not air or water."),

            new Question("Where does the light signal go so we understand what we see?",
                    new String[]{"Nose", "Brain", "Ear"}, 1,
                    "The brain processes visual signals."),

            new Question("What is the clear part at the front of the eye that helps focus light?",
                    new String[]{"Retina", "Cornea", "Lens"}, 1,
                    "The cornea is the clear outer layer that focuses light."),

            new Question("Which part of the eye changes light into messages for the brain?",
                    new String[]{"Retina", "Eyelid", "Pupil"}, 0,
                    "The retina converts light into neural signals."),

            new Question("What part of the eye gets bigger in the dark?",
                    new String[]{"Iris", "Cornea", "Pupil"}, 2,
                    "The pupil enlarges in the dark to allow more light in.")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        audioHelper = new AudioHelper(this);

        questionText = findViewById(R.id.questionText);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);

        loadQuestion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConstraintLayout layout = findViewById(R.id.quizbg);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(0);
        animationDrawable.setExitFadeDuration(0);
        animationDrawable.start();
    }
    private void loadQuestion() {
        if (currentQuestion < questions.length) {
            Question q = questions[currentQuestion];
            questionText.setText(q.question);
            optionA.setText(q.options[0]);
            optionB.setText(q.options[1]);
            optionC.setText(q.options[2]);
        }
    }

    public void onAnswerClick(View view) {
        int selectedIndex = -1;

        if (view == optionA) selectedIndex = 0;
        else if (view == optionB) selectedIndex = 1;
        else if (view == optionC) selectedIndex = 2;

        Question q = questions[currentQuestion];

        boolean isCorrect = (selectedIndex == q.correctIndex);
        if (isCorrect) score++;

        showResultDialog(isCorrect, q.explanation);
    }

    private void showResultDialog(boolean isCorrect, String explanation) {
        // Play appropriate sound
        if (isCorrect) {
            audioHelper.playCorrect();
        } else {
            audioHelper.playWrong();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isCorrect ? "Correct!" : "Oops!");
        builder.setMessage(isCorrect ? "Good job!" : "Explanation: " + explanation);
        builder.setCancelable(false);
        builder.setPositiveButton("Next", (dialog, which) -> {
            currentQuestion++;
            if (currentQuestion < questions.length) {
                loadQuestion();
            } else {
                showFinalScore();  // Show final score when done
            }
        });
        builder.show();
    }

    private void showFinalScore() {
        audioHelper.gameOver();  // Play correct sound once at the end

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Completed!");
        builder.setMessage("You got " + score + " out of " + questions.length + " correct.");
        builder.setCancelable(false);
        builder.setPositiveButton("Return to Home", (dialog, which) -> {
            finish(); // Close the activity or navigate as needed
        });
        builder.show();
    }

    public void onPrevClick(View view) {
        finish();
    }

    public void onAudioClick(View view) {
        audioHelper.showCustomVolumeDialog();
    }
}
