package com.example.howdoesthehumaneyeseethings;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.GestureDetector;

public class ZoomImageView extends AppCompatImageView {
    private GestureDetector gestureDetector;
    private float defaultScale = 1f;

    private Matrix matrix = new Matrix();
    private ScaleGestureDetector scaleDetector;
    private float[] matrixValues = new float[9];

    private float scale = 1f;
    private PointF lastTouch = new PointF();
    private int mode = 0; // 0 = none, 1 = drag

    private boolean isImageInitialized = false;

    private void centerImageInitially() {
        if (getDrawable() == null) return;

        float viewWidth = getWidth();
        float viewHeight = getHeight();

        int imageWidth = getDrawable().getIntrinsicWidth();
        int imageHeight = getDrawable().getIntrinsicHeight();

        float scaleX = viewWidth / imageWidth;
        float scaleY = viewHeight / imageHeight;
        float minScale = Math.min(scaleX, scaleY); // Fit image inside view

        scale = minScale;

        float dx = (viewWidth - imageWidth * minScale) / 2f;
        float dy = (viewHeight - imageHeight * minScale) / 2f;

        matrix.setScale(minScale, minScale);
        matrix.postTranslate(dx, dy);
        setImageMatrix(matrix);

        isImageInitialized = true;
    }

    public ZoomImageView(Context context) {
        super(context);
        init(context);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                float x = e.getX();
                float y = e.getY();

                if (scale <= defaultScale + 0.01f) {
                    // Zoom in to 2x at tapped point
                    float scaleFactor = 2.0f;
                    scale *= scaleFactor;
                    matrix.postScale(scaleFactor, scaleFactor, x, y);
                } else {
                    // Zoom out: reset scale and center image
                    scale = defaultScale;
                    centerImageInitially();
                    return true;
                }

                fixTranslation();
                setImageMatrix(matrix);
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        scaleDetector.onTouchEvent(event);

        PointF current = new PointF(event.getX(), event.getY());

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouch.set(current);
                mode = 1;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == 1 && !scaleDetector.isInProgress()) {
                    float dx = current.x - lastTouch.x;
                    float dy = current.y - lastTouch.y;

                    matrix.postTranslate(dx, dy);
                    fixTranslation(); // <-- clamp translation
                    setImageMatrix(matrix);

                    lastTouch.set(current.x, current.y);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mode = 0;
                break;
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();

            float newScale = scale * scaleFactor;
            if (newScale < 0.5f) {
                scaleFactor = 0.5f / scale;
            } else if (newScale > 5.0f) {
                scaleFactor = 5.0f / scale;
            }

            scale *= scaleFactor;

            float centerX = detector.getFocusX();
            float centerY = detector.getFocusY();
            matrix.postScale(scaleFactor, scaleFactor, centerX, centerY);
            fixTranslation(); // <-- clamp after scaling
            setImageMatrix(matrix);

            return true;
        }
    }

    private void fixTranslation() {
        RectF bounds = getImageBounds();
        float dx = getFixTranslation(bounds.left, bounds.right, getWidth());
        float dy = getFixTranslation(bounds.top, bounds.bottom, getHeight());
        matrix.postTranslate(dx, dy);
    }

    private float getFixTranslation(float start, float end, float viewSize) {
        float result = 0f;
        if (end - start <= viewSize) {
            result = viewSize / 2f - (start + end) / 2f; // center if smaller
        } else {
            if (start > 0) result = -start;
            if (end < viewSize) result = viewSize - end;
        }
        return result;
    }

    private RectF getImageBounds() {
        if (getDrawable() == null) return new RectF();

        matrix.getValues(matrixValues);
        float scaleX = matrixValues[Matrix.MSCALE_X];
        float scaleY = matrixValues[Matrix.MSCALE_Y];
        float transX = matrixValues[Matrix.MTRANS_X];
        float transY = matrixValues[Matrix.MTRANS_Y];

        int intrinsicWidth = getDrawable().getIntrinsicWidth();
        int intrinsicHeight = getDrawable().getIntrinsicHeight();

        float width = intrinsicWidth * scaleX;
        float height = intrinsicHeight * scaleY;

        return new RectF(transX, transY, transX + width, transY + height);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!isImageInitialized) {
            centerImageInitially();
        }
    }
}
