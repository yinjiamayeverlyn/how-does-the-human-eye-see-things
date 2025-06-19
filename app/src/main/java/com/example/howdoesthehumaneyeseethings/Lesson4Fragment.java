package com.example.howdoesthehumaneyeseethings;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Lesson4Fragment extends Fragment {

    public Lesson4Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout file
        View rootView =  inflater.inflate(R.layout.fragment4, container, false);

        rootView.findViewById(R.id.imagelineillusion).setOnClickListener(v -> showZoomDialog(R.drawable.line_fact));
        rootView.findViewById(R.id.imagemovingsnake).setOnClickListener(v -> showZoomDialog(R.drawable.moving_snack));
        rootView.findViewById(R.id.imagetroxlereffect).setOnClickListener(v -> showZoomDialog(R.drawable.troxler_effect));

        // Hint toggles
        toggleHint(rootView, R.id.showHint1, R.id.hint1);
        toggleHint(rootView, R.id.showHint2, R.id.hint2);
        toggleHint(rootView, R.id.showHint3, R.id.hint3);

        return rootView;
    }

    private void toggleHint(View rootView, int toggleTextViewId, int hintTextViewId) {
        TextView toggleView = rootView.findViewById(toggleTextViewId);
        TextView hintView = rootView.findViewById(hintTextViewId);

        // Initially hide the hint
        hintView.setVisibility(View.GONE);

        toggleView.setOnClickListener(v -> {
            if (hintView.getVisibility() == View.GONE) {
                hintView.setVisibility(View.VISIBLE);
                toggleView.setText(R.string.hide_hint_text);
            } else {
                hintView.setVisibility(View.GONE);
                toggleView.setText(R.string.show_hint_text);
            }
        });
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