package com.example.lab5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Compass extends View {

    private final Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float azimuth = 0f;

    public Compass(Context context) {
        super(context);
        init();
    }

    public Compass(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Compass(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(8f);
        circlePaint.setColor(Color.DKGRAY);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(48f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        arrowPaint.setColor(Color.RED);
        arrowPaint.setStrokeWidth(12f);
        arrowPaint.setStyle(Paint.Style.STROKE);

        centerPaint.setColor(Color.BLACK);
        centerPaint.setStyle(Paint.Style.FILL);

        markPaint.setColor(Color.GRAY);
        markPaint.setStrokeWidth(4f);
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int cx = width / 2;
        int cy = height / 2;

        int radius = Math.min(width, height) / 2 - 40;

        canvas.drawCircle(cx, cy, radius, circlePaint);

        for (int i = 0; i < 360; i += 30) {
            double rad = Math.toRadians(i - 90);
            float startX = (float) (cx + (radius - 20) * Math.cos(rad));
            float startY = (float) (cy + (radius - 20) * Math.sin(rad));
            float endX = (float) (cx + radius * Math.cos(rad));
            float endY = (float) (cy + radius * Math.sin(rad));
            canvas.drawLine(startX, startY, endX, endY, markPaint);
        }

        drawDirectionLabel(canvas, "N", cx, cy, radius, 0);
        drawDirectionLabel(canvas, "E", cx, cy, radius, 90);
        drawDirectionLabel(canvas, "S", cx, cy, radius, 180);
        drawDirectionLabel(canvas, "W", cx, cy, radius, 270);

        canvas.save();
        canvas.rotate(-azimuth, cx, cy);

        canvas.drawLine(cx, cy + 40, cx, cy - radius + 60, arrowPaint);
        canvas.drawLine(cx, cy - radius + 60, cx - 25, cy - radius + 95, arrowPaint);
        canvas.drawLine(cx, cy - radius + 60, cx + 25, cy - radius + 95, arrowPaint);

        canvas.restore();

        canvas.drawCircle(cx, cy, 12, centerPaint);
    }

    private void drawDirectionLabel(Canvas canvas, String text, int cx, int cy, int radius, int angleDeg) {
        double rad = Math.toRadians(angleDeg - 90);
        float x = (float) (cx + (radius - 60) * Math.cos(rad));
        float y = (float) (cy + (radius - 60) * Math.sin(rad)) + 18;
        canvas.drawText(text, x, y, textPaint);
    }
}