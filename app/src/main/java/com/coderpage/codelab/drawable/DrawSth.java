package com.coderpage.codelab.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author lc. 2017-08-28
 * @since 0.1.0
 */

public class DrawSth extends View {

    Paint mPaint1;
    Paint mPaint2;
    Paint mPaint3;

    public DrawSth(Context context) {
        this(context, null);
    }

    public DrawSth(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int rectSize = Math.min(measuredWidth, measuredHeight) / 8;

        float left = measuredWidth / 2 - rectSize / 2;
        float top = rectSize / 2;
        float right = left + rectSize;
        float bottom = top + rectSize;

        mPaint1.setColor(Color.BLACK);
        canvas.drawRect(left, top, right, bottom, mPaint1);

        canvas.save();
        canvas.rotate(30);

        top = bottom + rectSize / 2;
        bottom = top + rectSize;

        mPaint2.setColor(Color.BLUE);
        canvas.drawRect(left, top, right, bottom, mPaint2);

        canvas.restore();

        top = bottom + rectSize / 2;
        bottom = top + rectSize;

        mPaint3.setColor(Color.RED);
        canvas.drawRect(left, top, right, bottom, mPaint3);
    }
}
