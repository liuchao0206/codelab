package com.coderpage.codelab.widget.datepick;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.widget.OverScroller;

import com.coderpage.codelab.codelab.R;

/**
 * @author lc. 2019-07-15 10:36
 * @since 0.7.0
 */
public class WheelView extends View {
    private static final String TAG = "WheelV";

    /** 是否循环滚动 */
    private boolean mLoop;

    /** item 间距 */
    private int mItemDivider;

    /** 最近一次 touch x,y */
    private float mLastX;
    private float mLastY;

    private int mTouchSlop;

    /** 滚动速率的大小区间 */
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;

    /** View 中心(x,y) */
    private float mCenterY;
    private float mCenterX;

    private float mDrawHeightHalf;

    /** 文字大小 */
    private int mTextSize;
    /** 文字颜色-选中状态 */
    private int mTextColorAccent = Color.BLACK;
    /** 文字颜色-弱化的状态 */
    private int mTextColorWeak = Color.BLACK;

    /** 显示的数量 */
    private int mDisplayCount = 5;
    /** 当前选中的位置 */
    private int mCurrentPosition = 0;
    /** 中间文字的 baseLine */
    private float mCenterTextBaseLine = 0;

    /** 所有 ITEM 的 baseLine */
    private float[] mBaseLineArray = new float[0];
    /** 最长一个 ITEM 的宽高 */
    private Rect mMaxTextBounds = new Rect();
    /** 所有 ITEM 的宽高 */
    private Rect[] mTextBoundsArray = new Rect[0];
    private IWheelAdapter mAdapter;
    private WheelListener mListener;

    private Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
    private TextPaint mTextPaint;

    /** 检查用户滑动速度 */
    private VelocityTracker mVelocityTracker;
    /** 根据滑动速度，关系滑动一段距离 */
    private FlingHelper mFlingHelper;
    /** 缓慢滑动到选中的 ITEM */
    private SmoothScrollHelper mSmoothScrollHelper;
    /** 处理颜色平滑过渡 */
    private ColorHelper mColorHelper;

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            int itemCount = mAdapter != null ? mAdapter.getCount() : 0;
            mCurrentPosition = Math.min(itemCount - 1, mCurrentPosition);
            mCurrentPosition = Math.max(0, mCurrentPosition);
            calculateTextBounds();
            calculateCurrentBaseLine();
            setSelectPosition(mCurrentPosition);
        }

        @Override
        public void onInvalidated() {
            // do-nothing
        }
    };

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.WheelView_wvTextSize, dp2Px(14));
        mTextColorAccent = typedArray.getColor(R.styleable.WheelView_wvTextColorAccent, Color.BLACK);
        mTextColorWeak = typedArray.getColor(R.styleable.WheelView_wvTextColorWeak, Color.BLACK);
        mItemDivider = typedArray.getDimensionPixelSize(R.styleable.WheelView_wvItemDivider, dp2Px(8));
        mLoop = typedArray.getBoolean(R.styleable.WheelView_wvLoop, false);
        mDisplayCount = typedArray.getInt(R.styleable.WheelView_wvItemDisplayCount, 5);
        typedArray.recycle();

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColorAccent);
        mTextPaint.getFontMetrics(mFontMetrics);

        ViewConfiguration vc = ViewConfiguration.get(context);
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mTouchSlop = vc.getScaledTouchSlop();

        mFlingHelper = new FlingHelper();
        mSmoothScrollHelper = new SmoothScrollHelper();
        mColorHelper = new ColorHelper(mTextColorAccent, mTextColorWeak);
    }

    public void setAdapter(IWheelAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataSetObserver);

        int itemCount = mAdapter != null ? mAdapter.getCount() : 0;
        mCurrentPosition = Math.min(itemCount - 1, mCurrentPosition);
        mCurrentPosition = Math.max(0, mCurrentPosition);
        calculateTextBounds();
        calculateCurrentBaseLine();
        setSelectPosition(mCurrentPosition);
    }

    public void setListener(WheelListener listener) {
        mListener = listener;
    }

    public void setSelectPosition(int position) {
        int itemCount = getItemCount();
        if (position < 0 || position >= itemCount) {
            Log.e(TAG, "invalid position " + position + "  totalCount=" + itemCount);
            return;
        }
        mCurrentPosition = position;
        mSmoothScrollHelper.stop();
        mSmoothScrollHelper.smooth2FinalPosition();

        if (mListener != null) {
            mListener.onItemSelect(mCurrentPosition);
        }
    }

    public int getSelectPosition() {
        return mCurrentPosition;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 停止当前滑动
                mSmoothScrollHelper.stop();
                mFlingHelper.stop();

                mLastX = event.getX();
                mLastY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);

                // 滑动
                float dy = event.getY() - mLastY;
                mLastY = event.getY();
                scrollBy(dy);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaxFlingVelocity);

                float xVel = mVelocityTracker.getXVelocity();
                float yVel = mVelocityTracker.getYVelocity() / 2;
                fling(xVel, yVel);

                mVelocityTracker.clear();
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));

        mCenterX = getMeasuredWidth() / 2F;
        mCenterY = getMeasuredHeight() / 2F;

        mDrawHeightHalf = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2F;
        calculateCurrentBaseLine();
        mSmoothScrollHelper.stop();
        smoothToFinalPosition();
    }

    /** 测量宽度 */
    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        int result;

        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
                break;

            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = mMaxTextBounds.width() + getPaddingLeft() + getPaddingRight();
                break;

            default:
                result = size;
                break;
        }

        if (MeasureSpec.AT_MOST == mode) {
            result = Math.min(result, size);
        }

        return result;
    }

    /** 测量高度 */
    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        int result;

        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
                break;

            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = mMaxTextBounds.height() * mDisplayCount + mItemDivider * (mDisplayCount - 1) + getPaddingTop() + getPaddingBottom();
                break;

            default:
                result = size;
                break;
        }

        if (MeasureSpec.AT_MOST == mode) {
            result = Math.min(result, size);
        }

        return result;
    }

    private int getItemCount() {
        return mAdapter == null ? 0 : mAdapter.getCount();
    }

    /** 计算文字大小 */
    private void calculateTextBounds() {
        mMaxTextBounds.setEmpty();

        int itemCount = mAdapter != null ? mAdapter.getCount() : 0;
        if (mTextBoundsArray.length != itemCount) {
            mTextBoundsArray = new Rect[itemCount];
        }

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeightF = Math.abs(fontMetrics.leading) + Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent);
        int fontHeight = (int) Math.ceil(fontHeightF);

        // 遍历所有文字，计算最大值
        for (int i = 0; i < itemCount; i++) {
            Rect textBounds = mTextBoundsArray[i] != null ? mTextBoundsArray[i] : new Rect();
            String text = mAdapter.getText(i);

            mTextPaint.getTextBounds(text, 0, text.length(), textBounds);
            textBounds.top = 0;
            textBounds.bottom = fontHeight;
            if (textBounds.width() > mMaxTextBounds.width()) {
                mMaxTextBounds.set(textBounds);
            }

            mTextBoundsArray[i] = textBounds;
        }
    }

    /** 计算所有文字的 BaseLine */
    private void calculateCurrentBaseLine() {
        int itemCount = getItemCount();
        if (mBaseLineArray.length != itemCount) {
            mBaseLineArray = new float[itemCount];
        }
        // 文字绘制区域 top、bottom
        int top = getPaddingTop();
        int bottom = getMeasuredHeight() - getPaddingBottom();

        // 计算显示在中间选中 ITEM 的绘制 baseline
        mCenterTextBaseLine = (bottom + top - mFontMetrics.bottom - mFontMetrics.top) / 2;
        float fontHeight = mFontMetrics.bottom - mFontMetrics.top;

        // 中间选中 ITEM 的 baseLine
        setItemBaseLine(mCurrentPosition, mCenterTextBaseLine);

        // 上面部分的 ITEM
        int count = 0;
        for (int i = mCurrentPosition - 1; i >= 0; i--) {
            count++;
            float baseLine = mCenterTextBaseLine - (fontHeight + mItemDivider) * count;
            setItemBaseLine(i, baseLine);
        }

        // 下面部分的 ITEM
        count = 0;
        for (int i = mCurrentPosition + 1; i < itemCount; i++) {
            count++;
            float baseLine = mCenterTextBaseLine + (fontHeight + mItemDivider) * count;
            setItemBaseLine(i, baseLine);
        }
    }

    private void setItemBaseLine(int position, float baseLine) {
        int count = mAdapter != null ? mAdapter.getCount() : 0;
        if (count == 0) {
            return;
        }
        if (mBaseLineArray == null || mBaseLineArray.length < count) {
            mBaseLineArray = new float[count];
        }
        mBaseLineArray[position] = baseLine;
    }

    /**
     * 滑动。重新计算每个 ITEM 的位置
     *
     * @param dy 滑动距离
     */
    private void scrollBy(float dy) {
        if (dy == 0) {
            return;
        }

        if (dy > 0) {
            float firstItemBaseLine = mBaseLineArray.length == 0 ? 0 : mBaseLineArray[0];
            float dyMax = mCenterTextBaseLine - firstItemBaseLine;
            dy = Math.min(dy, dyMax);
        }
        // 想上拖拽，最后一个 item 不能超过中间
        if (dy < 0) {
            float lastItemBaseLine = mBaseLineArray.length == 0 ? 0 : mBaseLineArray[mBaseLineArray.length - 1];
            float dyMax = mCenterTextBaseLine - lastItemBaseLine;
            dy = Math.max(dy, dyMax);
        }

        // 计算每个 ITEM 的位置
        for (int i = 0; i < mBaseLineArray.length; i++) {
            mBaseLineArray[i] = mBaseLineArray[i] + dy;
        }
        // 绘制
        invalidate();
    }

    /**
     * 滑动
     *
     * @param xVelocity x-方向滑动速度
     * @param yVelocity y-方向滑动速度
     */
    private void fling(float xVelocity, float yVelocity) {
        // 合法的滚动速率值
        float yVelocityFinal = yVelocity;
        if (Math.abs(yVelocity) < mMinFlingVelocity) {
            yVelocityFinal = 0;
        }
        yVelocityFinal = Math.min(yVelocityFinal, mMaxFlingVelocity);

        mFlingHelper.fling((int) xVelocity, (int) yVelocityFinal);
    }

    /** 平滑滚动到最终位置 */
    private void smoothToFinalPosition() {

        int selectPosition = 0;
        float minDistanceToCenter = Float.MAX_VALUE;
        for (int i = 0; i < mBaseLineArray.length; i++) {
            float distanceToCenter = Math.abs(mBaseLineArray[i] - mCenterTextBaseLine);
            if (distanceToCenter < minDistanceToCenter) {
                minDistanceToCenter = distanceToCenter;
                selectPosition = i;
            }
        }

        mCurrentPosition = selectPosition;
        mSmoothScrollHelper.stop();
        mSmoothScrollHelper.smooth2FinalPosition();

        if (mListener != null) {
            mListener.onItemSelect(mCurrentPosition);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int top = getPaddingTop();
        int bottom = getMeasuredHeight() - getPaddingBottom();
        int width = getMeasuredWidth();

        int itemCount = mAdapter != null ? mAdapter.getCount() : 0;
        for (int i = 0; i < itemCount; i++) {
            float baseLine = mBaseLineArray[i];
            Rect textBounds = mTextBoundsArray[i];
            if (baseLine < top || baseLine > bottom + textBounds.height()) {
                continue;
            }

            float fraction = Math.abs(mCenterTextBaseLine - baseLine) / mDrawHeightHalf;
            int color = mColorHelper.getColor(Math.min(fraction, 1));
            mTextPaint.setColor(color);

            String text = mAdapter.getText(i);
            canvas.drawText(text, Math.abs((width - textBounds.width()) / 2), baseLine, mTextPaint);
        }
    }

    private int dp2Px(float dpValue) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics);
    }

    class FlingHelper implements Runnable {

        private OverScroller mScroller;

        private int mLastFlingY;

        private FlingHelper() {
            mScroller = new OverScroller(getContext());
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                int currY = mScroller.getCurrY();
                scrollBy(currY - mLastFlingY);
                mLastFlingY = currY;
                ViewCompat.postOnAnimation(WheelView.this, this);
            } else {
                smoothToFinalPosition();
            }
        }

        void fling(int xVelocity, int yVelocity) {
            mLastFlingY = 0;
            mScroller.fling(0, 0, xVelocity, yVelocity, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            removeCallbacks(this);
            ViewCompat.postOnAnimation(WheelView.this, this);
        }

        void stop() {
            removeCallbacks(this);
            mScroller.abortAnimation();
        }
    }

    class SmoothScrollHelper {

        private AccelerateInterpolator mInterpolator;
        private ValueAnimator mAnimator;
        private float mLastAnimateVal;

        SmoothScrollHelper() {
            mInterpolator = new AccelerateInterpolator();
            mAnimator = new ValueAnimator();
        }

        void smooth2FinalPosition() {
            Log.d(TAG, "smooth2FinalPosition");
            if (mBaseLineArray.length <= mCurrentPosition) {
                return;
            }
            float selectItemBaseLine = mBaseLineArray[mCurrentPosition];
            Log.d(TAG, "mCurrentPosition : " + mCurrentPosition + " selectItemBaseLine=" + selectItemBaseLine);
            float distance = mCenterTextBaseLine - selectItemBaseLine;
            Log.d(TAG, "mCurrentPosition : " + mCurrentPosition + " distance=" + distance);
            mAnimator.setInterpolator(mInterpolator);
            mAnimator.setDuration(200);
            mAnimator.setFloatValues(0, distance);
            mLastAnimateVal = 0;
            mAnimator.addUpdateListener(animation -> {
                float animateVal = (float) animation.getAnimatedValue();
                float dy = (float) animation.getAnimatedValue() - mLastAnimateVal;
                mLastAnimateVal = animateVal;
                scrollBy(dy);
            });
            mAnimator.start();
        }

        void stop() {
            if (mAnimator.isStarted() || mAnimator.isRunning()) {
                mAnimator.removeAllUpdateListeners();
                mAnimator.cancel();
            }
        }
    }

    class ColorHelper {

        private int alphaDiff;
        private int redDiff;
        private int blueDiff;
        private int greenDiff;

        private int redStart;
        private int blueStart;
        private int greenStart;
        private int alphaStart;

        ColorHelper(int startColor, int endColor) {
            redStart = Color.red(startColor);
            blueStart = Color.blue(startColor);
            greenStart = Color.green(startColor);
            alphaStart = Color.alpha(startColor);

            int redEnd = Color.red(endColor);
            int blueEnd = Color.blue(endColor);
            int greenEnd = Color.green(endColor);
            int alphaEnd = Color.alpha(endColor);

            alphaDiff = alphaEnd - alphaStart;
            redDiff = redEnd - redStart;
            blueDiff = blueEnd - blueStart;
            greenDiff = greenEnd - greenStart;
        }

        int getColor(float fraction) {
            int alphaCurrent = (int) (alphaStart + fraction * alphaDiff);
            int redCurrent = (int) (redStart + fraction * redDiff);
            int blueCurrent = (int) (blueStart + fraction * blueDiff);
            int greenCurrent = (int) (greenStart + fraction * greenDiff);

            return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
        }
    }
}
