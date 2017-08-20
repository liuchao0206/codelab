package com.coderpage.codelab.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2017-08-20
 * @since 0.1.0
 *
 * 参考了鸿洋大神的博客：http://blog.csdn.net/lmj623565791/article/details/38352503/
 */

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获取父 View 为其设置的大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 获取父 View 为其设置的测量模式 (参考鸿洋大神博客 http://blog.csdn.net/lmj623565791/article/details/38339817)
        // MeasureSpec.EXACTLY; 表示设置了精确的值，一般当childView设置其宽、高为精确值、match_parent时，ViewGroup会将其设置为EXACTLY；
        // MeasureSpec.AT_MOST; 表示子布局被限制在一个最大值内，一般当childView设置其宽、高为wrap_content时，ViewGroup会将其设置为AT_MOST；
        // MeasureSpec.UNSPECIFIED; 表示子布局想要多大就多大，一般出现在AadapterView的item的heightMode中、ScrollView的childView的heightMode中；此种模式比较少见。
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = 0;
        int lineWidth = 0;
        int lineMaxHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 测量 child 大小
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            // 获取 child layoutParams
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            // 测量大小
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            // 如果本行加入该 child 则会超出目前的最大宽度，换行
            if (lineWidth + childWidth > sizeWidth) {
                width = Math.max(width, Math.max(childWidth, lineWidth)); // 取最大的宽度值
                height += lineMaxHeight; // 累加高度

                lineWidth = childWidth;
                lineMaxHeight = childHeight;
            } else {
                // 否则，累加当前行的宽度，计算当前行的最大高度
                lineWidth += childWidth;
                lineMaxHeight = Math.max(lineMaxHeight, childHeight);
            }

            // 如果是最后一个 child，累加 height
            if (i == childCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineMaxHeight;
            }
        }

        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height);
    }

    private List<List<View>> viewsInLine = new ArrayList<>();
    private List<Integer> lineHeightList = new ArrayList<>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 计算 child 总行数，每行 child，每行的高度
        viewsInLine.clear();
        lineHeightList.clear();

        int width = getWidth();
        int lineWidth = 0;
        int lineHeight = 0;

        List<View> lineViews = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if (lineWidth + childWidth > width) {
                lineHeightList.add(lineHeight);
                viewsInLine.add(lineViews);
                lineWidth = 0;
                lineHeight = childHeight;
                lineViews = new ArrayList<>();
            } else {
                lineViews.add(child);
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
        }

        viewsInLine.add(lineViews);
        lineHeightList.add(lineHeight);

        int left = 0;
        int top = 0;

        int lineNum = viewsInLine.size();
        for (int i = 0; i < lineNum; i++) {
            lineViews = viewsInLine.get(i);
            lineHeight = lineHeightList.get(i);

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == GONE) continue;
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                int lc = left + lp.leftMargin;
                int rc = lc + child.getMeasuredWidth();
                int tc = top + lp.topMargin;
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);
                left = rc + lp.rightMargin;
            }

            left = 0;
            top += lineHeight;
        }
    }
}
