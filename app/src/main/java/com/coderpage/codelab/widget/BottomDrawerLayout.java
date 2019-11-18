package com.coderpage.codelab.widget;

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * @author lc. 2019-06-26 10:16
 * @since 0.7.0
 */
public class BottomDrawerLayout extends FrameLayout {

    private float mDownY;
    private float mLastPositionY;

    public BottomDrawerLayout(@NonNull Context context) {
        super(context);
    }

    public BottomDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                mLastPositionY = mDownY;
                break;

            case MotionEvent.ACTION_MOVE:
                int offset = (int) (getY() - mLastPositionY);
                mLastPositionY = getY();
                ViewCompat.offsetTopAndBottom(this, offset);
                break;

            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

}
