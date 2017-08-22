package com.coderpage.codelab.animation;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import static com.coderpage.codelab.utils.LogUtils.LOGE;
import static com.coderpage.codelab.utils.LogUtils.makeLogTag;

/**
 * @author lc. 2017-08-21
 * @since 0.1.0
 */

public class SlidingCollapseLayout extends FrameLayout {
    private static final String TAG = makeLogTag(SlidingCollapseLayout.class);

    public SlidingCollapseLayout(Context context) {
        this(context, null);
    }

    public SlidingCollapseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingCollapseLayout(Context context, AttributeSet attrs,
                                 @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public SlidingCollapseLayout(Context context, AttributeSet attrs,
                                 @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    float startX;
    float startY;
    float currentX;
    float currentY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                LOGE(TAG, "ACTION_DOWN x=" + startX + "  y=" + startY);
                return true;
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                currentY = event.getY();
                LOGE(TAG, "ACTION_MOVE x=" + currentX + "  y=" + currentY);
                break;
            case MotionEvent.ACTION_CANCEL:
                float cancelX = event.getX();
                float cancelY = event.getY();
                LOGE(TAG, "ACTION_CANCEL x=" + cancelX + "  y=" + cancelY);
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                LOGE(TAG, "ACTION_UP x=" + upX + "  y=" + upY);
                break;
        }
        return super.onTouchEvent(event);
    }
}
