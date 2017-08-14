package com.coderpage.codelab.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.coderpage.codelab.codelab.R;

import java.text.DecimalFormat;

/**
 * Created by liuchao on 2017/8/13.
 */

public class RoundPercentBar extends View {
    private static final String TAG = RoundPercentBar.class.getSimpleName();

    private Paint mPaint;
    private TextPaint mTextPaint;
    private TextPaint mPercentValuePaint;
    private TextPaint mHintPaint;
    private RectF mRectF;
    private Settings mSettings;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.0");

    public RoundPercentBar(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public RoundPercentBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public RoundPercentBar(Context context,
                           AttributeSet attrs,
                           int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoundPercentBar(Context context,
                           AttributeSet attrs,
                           int defStyleAttr,
                           int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context,
                      AttributeSet attrs,
                      int defStyleAttr,
                      int defStyleRes) {

        mPaint = new Paint();
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPercentValuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mHintPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mRectF = new RectF();
        mSettings = Settings.createDefault(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundPercentBar, defStyleAttr, defStyleRes);

        int baseBarColor = typedArray.getColor(R.styleable.RoundPercentBar_rpbBaseColor, 0);
        int percentBarColor = typedArray.getColor(R.styleable.RoundPercentBar_rpbPercentBarColor, 0);
        int barWidth = typedArray.getDimensionPixelSize(R.styleable.RoundPercentBar_rpbBarWidth, 0);
        boolean drawPercentValue = typedArray.getBoolean(
                R.styleable.RoundPercentBar_rpbDrawPercentVal, true);
        int textColor = typedArray.getColor(R.styleable.RoundPercentBar_rpbTextColor, 0);
        int percentTextColor = typedArray.getColor(R.styleable.RoundPercentBar_rpbPercentTextColor, 0);
        int hintTextColor = typedArray.getColor(R.styleable.RoundPercentBar_rpbHintTextColor, 0);
        int textSize = typedArray.getDimensionPixelSize(R.styleable.RoundPercentBar_rpbTextSize, 0);
        int percentTextSize = typedArray.getDimensionPixelSize(
                R.styleable.RoundPercentBar_rpbPercentTextSize, 0);
        int hintTextSize = typedArray.getDimensionPixelSize(
                R.styleable.RoundPercentBar_rpbHintTextSize, 0);
        String hintText = typedArray.getString(R.styleable.RoundPercentBar_rpbHintText);

        if (baseBarColor != 0) mSettings.setBaseBarColor(baseBarColor);
        if (percentBarColor != 0) mSettings.setPercentBarColor(percentBarColor);
        if (barWidth != 0) mSettings.setBarWidth(barWidth);
        mSettings.setDrawPercentValue(drawPercentValue);
        if (textColor != 0) mSettings.setTextColor(textColor);
        if (percentTextColor != 0) mSettings.setPercentTextColor(percentTextColor);
        if (hintTextColor != 0) mSettings.setHintTextColor(hintTextColor);
        if (textSize != 0) mSettings.setTextSize(textSize);
        if (percentTextSize != 0) mSettings.setPercentTextSize(percentTextSize);
        if (hintTextSize != 0) mSettings.setHintTextSize(hintTextSize);
        mSettings.setHintText(hintText);

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.i(TAG, "onMeasure: widthMeasureSpec=" + widthMeasureSpec
                + "  heightMeasureSpec=" + heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed,
                            int left,
                            int top,
                            int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Log.i(TAG, "onLayout: changed=" + changed + " left="
                + left + " top=" + top + " right=" + right + " bottom=" + bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setAntiAlias(true); // 抗锯齿
        mPaint.setStrokeCap(Paint.Cap.ROUND); // 圆头画笔
        mPaint.setColor(mSettings.baseBarColor);
        mPaint.setStrokeWidth(mSettings.barWidth); // 圆环宽度
        mPaint.setStyle(Paint.Style.STROKE);

        int x = getWidth() / 2;
        int y = getHeight() / 2;
        int center = Math.min(x, y); // 圆心
        int radius = x - dip2px(getContext(), mSettings.barWidth) / 2; // 半径

        float startAngle = 0;
        float sweepAngle = 360;

        // 画底部圆
        mRectF.set(center - radius, center - radius,
                center + radius, center + radius);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);

        // 设置圆环颜色
        mPaint.setColor(mSettings.percentBarColor);

        startAngle = 90;
        sweepAngle = 360 * mSettings.percentValue;

        // 画百分比圆环
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);

        // 画文字
        drawText(canvas);
    }


    private void drawText(Canvas canvas) {
        mTextPaint.setTextSize(mSettings.textSize);
        mTextPaint.setColor(mSettings.textColor);
        mPercentValuePaint.setTextSize(mSettings.percentTextSize);
        mPercentValuePaint.setColor(mSettings.percentTextColor);
        mHintPaint.setTextSize(mSettings.hintTextSize);
        mHintPaint.setColor(mSettings.hintTextColor);

        float textHeight = mTextPaint.descent() + mTextPaint.ascent();
        float percentTextHeight = mPercentValuePaint.descent() + mPercentValuePaint.ascent();

        if (isDrawText()) {
            float textWidth = mTextPaint.measureText(mSettings.text);
            float textX = getWidth() / 2 - textWidth / 2;
            float textY;
            if (isDrawPercentValue()) {
                textY = getHeight() / 2 + textHeight / 2;
            } else {
                textY = getHeight() / 2 - textHeight / 2;
            }
            canvas.drawText(mSettings.text, textX, textY, mTextPaint);
        }

        if (isDrawPercentValue()) {
            String percentValueText = mSettings.percentText;
            if (TextUtils.isEmpty(percentValueText)) {
                percentValueText = mDecimalFormat.format(mSettings.percentValue * 100) + "%";
            }
            float textWidth = mPercentValuePaint.measureText(percentValueText);
            float textX = getWidth() / 2 - textWidth / 2;
            float textY;
            if (isDrawText()) {
                textY = getHeight() / 2 - percentTextHeight + dip2px(getContext(), 4);
            } else {
                textY = getHeight() / 2 - percentTextHeight / 2;
            }
            canvas.drawText(percentValueText, textX, textY, mPercentValuePaint);
        }

        if (!isDrawText() && !isDrawPercentValue()
                && !TextUtils.isEmpty(mSettings.hintText)) {
            float textWidth = mHintPaint.measureText(mSettings.hintText);
            float textX = getWidth() / 2 - textWidth / 2;
            float textY = getHeight() / 2 - percentTextHeight / 2;
            canvas.drawText(mSettings.hintText, textX, textY, mHintPaint);
        }
    }

    private boolean isDrawText() {
        return !TextUtils.isEmpty(getSettings().getText());
    }

    private boolean isDrawPercentValue() {
        return mSettings.drawPercentValue && mSettings.percentValue != 0;
    }

    public void setText(String text){
        getSettings().setText(text);
    }

    public void setPercent(float percent) {
        getSettings().setPercentValue(percent);
    }

    public Settings getSettings() {
        return mSettings;
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static class Settings {
        private boolean drawPercentValue;

        private float percentValue;

        private int baseBarColor;
        private int percentBarColor;
        private int barWidth;
        private int textColor;
        private int textSize;
        private int percentTextColor;
        private int percentTextSize;
        private int hintTextColor;
        private int hintTextSize;

        private String text;
        private String percentText;
        private String hintText;

        private Settings() {

        }

        private static Settings createDefault(Context context) {
            Settings settings = new Settings();

            settings.baseBarColor = Color.parseColor("#d1d1d1");
            settings.percentBarColor = Color.parseColor("#8c8eb9");
            settings.barWidth = dip2px(context, 6);
            settings.textSize = dip2px(context, 14);
            settings.percentTextSize = dip2px(context, 12);
            settings.hintTextSize = dip2px(context, 14);
            settings.textColor = Color.BLACK;
            settings.percentTextColor = Color.BLACK;
            settings.hintTextColor = Color.BLACK;

            return settings;
        }

        public boolean isDrawPercentValue() {
            return drawPercentValue;
        }

        public void setDrawPercentValue(boolean drawPercentValue) {
            this.drawPercentValue = drawPercentValue;
        }

        public float getPercentValue() {
            return percentValue;
        }

        public void setPercentValue(float percentValue) {
            this.percentValue = percentValue;
        }

        public int getBaseBarColor() {
            return baseBarColor;
        }

        public void setBaseBarColor(int baseBarColor) {
            this.baseBarColor = baseBarColor;
        }

        public int getPercentBarColor() {
            return percentBarColor;
        }

        public void setPercentBarColor(int percentBarColor) {
            this.percentBarColor = percentBarColor;
        }

        public int getBarWidth() {
            return barWidth;
        }

        public void setBarWidth(int barWidth) {
            this.barWidth = barWidth;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        public int getTextSize() {
            return textSize;
        }

        public void setTextSize(int textSize) {
            this.textSize = textSize;
        }

        public int getPercentTextColor() {
            return percentTextColor;
        }

        public void setPercentTextColor(int percentTextColor) {
            this.percentTextColor = percentTextColor;
        }

        public int getPercentTextSize() {
            return percentTextSize;
        }

        public void setPercentTextSize(int percentTextSize) {
            this.percentTextSize = percentTextSize;
        }

        public int getHintTextColor() {
            return hintTextColor;
        }

        public void setHintTextColor(int hintTextColor) {
            this.hintTextColor = hintTextColor;
        }

        public int getHintTextSize() {
            return hintTextSize;
        }

        public void setHintTextSize(int hintTextSize) {
            this.hintTextSize = hintTextSize;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getPercentText() {
            return percentText;
        }

        public void setPercentText(String percentText) {
            this.percentText = percentText;
        }

        public String getHintText() {
            return hintText;
        }

        public void setHintText(String hintText) {
            this.hintText = hintText;
        }
    }
}
