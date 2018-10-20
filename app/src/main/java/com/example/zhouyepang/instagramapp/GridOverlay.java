package com.example.zhouyepang.instagramapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GridOverlay extends View {
    private Paint linePainter;

    public GridOverlay(Context context) {
        super(context);
        init();
    }

    public GridOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePainter = new Paint();
        linePainter.setAntiAlias(true);
        linePainter.setColor(Color.BLACK);
        linePainter.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int viewWidth = this.getWidth();
        int viewHeight = this.getHeight();

        int width = viewWidth / 3;
        int height = viewHeight / 3;

        for (int i = width, j = 0; i < viewWidth && j < 3; i += width, j++) {
            canvas.drawLine(i, 0, i, viewHeight, linePainter);
        }
        for (int j = height, i = 0; j < viewHeight && i < 3; j += height, i++) {
            canvas.drawLine(0, j, viewWidth, j, linePainter);
        }
    }

}
