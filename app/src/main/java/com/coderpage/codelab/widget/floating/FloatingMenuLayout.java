package com.coderpage.codelab.widget.floating;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * @author liuchao. 2018-01-05 14:52
 * @since 0.5.1
 */

public class FloatingMenuLayout extends FrameLayout {

    private static final String TAG = "FloatingMenu";

    /** 菜单收起状态 */
    public static final int MENU_RETRACT = 0;
    /** 菜单展开状态 */
    public static final int MENU_EXPAND = 1;

    /** menuActionView 是否被 ViewDragHelper 捕获，如果被捕获说明正在拖拽中 */
    private boolean mDragViewOnCaptured = false;
    /** 是否是第一次 layout */
    private boolean mIsFirstLayout = true;

    /** 当前菜单状态 {@link #MENU_RETRACT} {@link #MENU_EXPAND} */
    private int mMenuState = MENU_RETRACT;
    /** 菜单 Item 间距值 */
    private int mMenuMargin;
    /** 菜单展开或收起动画执行时间 */
    private int mDuration = 200;
    /** 屏幕宽度 */
    private int mWindowWidth;

    private float mTouchDownX;
    private float mTouchDownY;

    /** 菜单展开、收起按钮，可拖拽 */
    private View mMenuActionView;
    /** 菜单 View */
    private View[] mMenuViewArray;

    /** 当前正在运行的菜单 收起or展开 动画，用于判断当前是否有动画在执行 */
    private AnimatorSet mCurrentMenuAnimator;
    /** 拖拽帮助类 */
    private final ViewDragHelper mDragHelper;

    private MenuStateChangeListener mMenuStateChangeListener;

    /** ViewDragHelper 回调 */
    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // 如果被捕获的 View 为 menuActionView，需要交给 ViewDragHelper 解析触屏事件并拖拽
            return child == mMenuActionView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            // 当 menuActionView 位置变化时，重绘 View
            invalidate();
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            // menuActionView 被捕获，将开始拖拽
            mDragViewOnCaptured = true;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            // menuActionView 被释放，拖拽结束
            mDragViewOnCaptured = false;
            makeMenuActionAlignSide();
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            // 返回 menuActionView 最新的 top 值
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mMenuActionView.getHeight();
            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // 返回 menuActionView 最新的 left 值
            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - mMenuActionView.getWidth();
            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
            return newLeft;
        }
    }

    public FloatingMenuLayout(Context context) {
        this(context, null);
    }

    public FloatingMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMenuLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        initView();
    }

    private void initView() {
        mWindowWidth = getContext().getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 如果第一次显示到屏幕，将菜单 item 隐藏（移除屏幕）
        if (mIsFirstLayout) {
            mIsFirstLayout = false;
            if (mMenuViewArray != null && mMenuViewArray.length > 0) {
                int windowWidth = getResources().getDisplayMetrics().widthPixels;
                for (View menu : mMenuViewArray) {
                    menu.setTranslationX(0);
                    ViewCompat.offsetLeftAndRight(menu, -windowWidth);
                }
            }
        }

    }

    /**
     * 设置菜单收起和展开动画时间，单位 ms
     */
    private void setDuration(int duration) {
        mDuration = duration;
    }

    /**
     * 设置菜单 item 间距，单位 px
     */
    public void setMenuMargin(int menuMargin) {
        mMenuMargin = menuMargin;
    }

    private void setMenuActionView(View menuActionView) {
        mMenuActionView = menuActionView;
        if (menuActionView.getParent() != null) {
            ((ViewGroup) menuActionView.getParent()).removeView(menuActionView);
        }
        addView(menuActionView);
    }

    private void setMenuViewArray(View... menuViews) {
        mMenuViewArray = menuViews;
        if (mMenuViewArray == null) {
            return;
        }

        for (View menu : mMenuViewArray) {
            if (menu.getParent() != null) {
                ((ViewGroup) menu.getParent()).removeView(menu);
            }
            addView(menu);
        }
    }

    /**
     * 返回当前菜单状态。
     *
     * 收起状态-{@link #MENU_RETRACT}，
     *
     * 展开-{@link #MENU_EXPAND}
     */
    public int menuState() {
        return mMenuState;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int actionMasked = ev.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = ev.getX();
                mTouchDownY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                // UP 事件，判断是否拖拽了
                boolean hasDrag = mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL | ViewDragHelper.DIRECTION_VERTICAL, mDragHelper.getActivePointerId());
                // 如果发生了拖拽，将 touch 事件交给 viewDragHelper 处理拖拽
                if (hasDrag) {
                    break;
                }
                // 如果没有发生拖拽，判断是否发生了点击事件
                // 如果点击到了 menuActionView，切换菜单收起、展开状态
                // 如果点击到了 menu item，通知 menu 被点击
                // 如果点击到了空白区域，如果正好是菜单展开的状态，需要将菜单收起

                // ACTION_DOWN 事件（手指落下）发生的位置下对应的 view
                View eventDownTouchedView = findTopChildUnder((int) mTouchDownX, (int) mTouchDownY);
                // ACTION_UP 事件（手指离开）发生的位置下对应的 view
                View eventUpTouchedView = findTopChildUnder((int) ev.getX(), (int) ev.getY());

                // 点击在空白区域, ps: 手指落下和离开的位置必须全部刚好对应同一个 view，才可认为该 view 发生了点击
                if (eventDownTouchedView == null || eventUpTouchedView == null || eventDownTouchedView != eventUpTouchedView) {
                    // 如果当前是展开状态，用户点击在了空白区域，则收起菜单
                    if (menuState() == MENU_EXPAND) {
                        retractMenus();
                    }
                    break;
                }

                // 如果点击的是 menuActionView
                if (eventUpTouchedView == mMenuActionView) {
                    if (menuState() == MENU_RETRACT) {
                        expandMenus();
                    } else if (menuState() == MENU_EXPAND) {
                        retractMenus();
                    }
                }

                // 如果点击的是 menu，将点击事件交给 menu 处理
                eventUpTouchedView.performClick();
                break;
            default:
                break;
        }

        // 如果 menu 为展开状态，中断所有 touch 事件，不允许拖拽，拖拽区域后的视图也无法点击
        if (menuState() == MENU_EXPAND) {
            return true;
        }

        // 将触屏事件交给 DragHelper 处理拖拽
        mDragHelper.processTouchEvent(ev);

        // 如果 dragView 为捕获状态(拖动中)，中断所有 touch 事件
        return mDragViewOnCaptured;
    }

    private View findTopChildUnder(int x, int y) {
        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (x >= child.getLeft() && x < child.getRight() && y >= child.getTop() && y < child.getBottom()) {
                return child;
            }
        }
        return null;
    }

    /** 移动 menuActionView 到屏幕边 */
    private void makeMenuActionAlignSide() {
        // 当前菜单是否依靠在屏幕的左端
        boolean alignLeft = mMenuActionView.getX() + mMenuActionView.getWidth() / 2 < getWidth() / 2;

        float startX = mMenuActionView.getX();
        float destinationX;

        if (alignLeft) {
            destinationX = 0;
        } else {
            destinationX = getWidth() - mMenuActionView.getWidth();
        }

        // 将控件移动到屏幕边缘
        ViewCompat.offsetLeftAndRight(mMenuActionView, (int) (destinationX - startX));
        invalidate();

        // 执行动画
        ObjectAnimator alignSideAnimator = ObjectAnimator.ofFloat(mMenuActionView, "x", startX, destinationX);
        alignSideAnimator.setInterpolator(new LinearInterpolator());
        alignSideAnimator.setDuration(200);
        alignSideAnimator.start();
    }

    /**
     * 展开菜单
     */
    public synchronized void expandMenus() {
        if (mMenuViewArray == null || mMenuViewArray.length == 0) {
            return;
        }

        // 如果当前有执行的动画，返回
        if (mCurrentMenuAnimator != null) {
            if (mCurrentMenuAnimator.isRunning() || mCurrentMenuAnimator.isStarted()) {
                return;
            }
        }

        float menuActionX = mMenuActionView.getX();
        int menuActionHeight = mMenuActionView.getHeight();
        int menuActionViewWidth = mMenuActionView.getWidth();

        // 当前菜单是否依靠在屏幕的左端
        boolean currentShowOnLeftSide = menuActionX + menuActionViewWidth / 2 < getWidth() / 2;

        if (currentShowOnLeftSide) {
            int displayAnchorX = mMenuActionView.getRight();
            // 计算 menu 显示的位置
            for (View menu : mMenuViewArray) {
                if (menu == null) {
                    continue;
                }
                int menuHeight = menu.getHeight();
                int menuDestinationOffsetX = displayAnchorX + mMenuMargin - menu.getLeft();
                int menuDestinationOffsetY = (mMenuActionView.getTop() - menu.getTop()) + (menuActionHeight - menuHeight) / 2;

                ViewCompat.offsetLeftAndRight(menu, menuDestinationOffsetX);
                ViewCompat.offsetTopAndBottom(menu, menuDestinationOffsetY);

                displayAnchorX = menu.getRight();
            }

        } else {
            int displayAnchorX = mMenuActionView.getLeft();
            // 计算 menu 显示的位置
            for (View menu : mMenuViewArray) {
                if (menu == null) {
                    continue;
                }
                int menuHeight = menu.getHeight();
                int menuDestinationOffsetX = displayAnchorX - mMenuMargin - menu.getRight();
                int menuDestinationOffsetY = (mMenuActionView.getTop() - menu.getTop()) + (menuActionHeight - menuHeight) / 2;

                ViewCompat.offsetLeftAndRight(menu, menuDestinationOffsetX);
                ViewCompat.offsetTopAndBottom(menu, menuDestinationOffsetY);

                displayAnchorX = menu.getLeft();
            }
        }

        // 重绘 menu
        invalidate();

        // menu 显示动画
        List<Animator> menuAnimatorList = new ArrayList<>(mMenuViewArray.length);
        for (View menu : mMenuViewArray) {
            if (menu == null) {
                continue;
            }

            PropertyValuesHolder pvhX;
            if (menu.getTranslationX() == 0) {
                pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, menuActionX - menu.getX(), 0);
            } else {
                pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0);
            }
            ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(menu, pvhX);
            animation.setDuration(mDuration);
            animation.setInterpolator(new OvershootInterpolator(0.9f));
            menuAnimatorList.add(animation);
        }

        AnimatorSet menuAnimatorSet = new AnimatorSet();
        menuAnimatorSet.playTogether(menuAnimatorList);
        menuAnimatorSet.start();

        mCurrentMenuAnimator = menuAnimatorSet;

        // 处理收起后的操作
        onMenuExpand();
    }

    /**
     * 收起菜单
     */
    public synchronized void retractMenus() {
        if (mMenuViewArray == null || mMenuViewArray.length == 0) {
            return;
        }

        // 如果当前有执行的动画，返回
        if (mCurrentMenuAnimator != null) {
            if (mCurrentMenuAnimator.isRunning() || mCurrentMenuAnimator.isStarted()) {
                return;
            }
        }

        float menuActionX = mMenuActionView.getX();
        // menu 收起动画
        List<Animator> menuAnimatorList = new ArrayList<>(mMenuViewArray.length);
        for (View menu : mMenuViewArray) {
            if (menu == null) {
                continue;
            }

            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, menuActionX - menu.getX());
            ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(menu, pvhX);
            animation.setDuration(mDuration);
            animation.setInterpolator(new OvershootInterpolator(0.9f));
            menuAnimatorList.add(animation);
        }

        AnimatorSet menuAnimatorSet = new AnimatorSet();
        menuAnimatorSet.playTogether(menuAnimatorList);
        menuAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 收起菜单动画结束后，将菜单 view 隐藏掉（移到屏幕外即可）
                if (mMenuViewArray != null && mMenuViewArray.length > 0) {
                    for (View menu : mMenuViewArray) {
                        ViewCompat.setTranslationX(menu, 0);
                        ViewCompat.offsetLeftAndRight(menu, -mWindowWidth);
                    }
                    invalidate();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        menuAnimatorSet.start();

        mCurrentMenuAnimator = menuAnimatorSet;
        // 处理收起后的操作
        onMenuRetract();
    }

    private void onMenuExpand() {
        mMenuState = MENU_EXPAND;
        if (mMenuStateChangeListener != null) {
            mMenuStateChangeListener.onStateChange(this,mMenuActionView, mMenuState);
        }
    }

    private void onMenuRetract() {
        mMenuState = MENU_RETRACT;
        if (mMenuStateChangeListener != null) {
            mMenuStateChangeListener.onStateChange(this,mMenuActionView, mMenuState);
        }
    }

    /**
     * 菜单状态监听，菜单收起、展开监听
     */
    public interface MenuStateChangeListener {
        /**
         * 菜单收起或展开时被回调
         *
         * @param layout         {@link FloatingMenuLayout}
         * @param menuActionView 菜单入口
         * @param currentState   菜单当前最新的状态{@link #MENU_EXPAND} {@link #MENU_RETRACT}
         */
        void onStateChange(FloatingMenuLayout layout, View menuActionView, int currentState);
    }

    public static class Builder {
        /** 菜单 Item 间距值 */
        private int mMenuMarginDip = 16;
        /** 菜单展开或收起动画执行时间 */
        private int mDuration = 200;
        /** 菜单入口初始化位置默认是 右边中间位置 */
        private int mMenuActionViewInitGravity = Gravity.CENTER_VERTICAL | Gravity.END;

        /** 菜单入口 */
        private View mMenuActionView;
        /** 菜单 item */
        private View[] mMenuViewArray;
        /** {@link FloatingMenuLayout} 的父控件 */
        private ViewGroup mParent;
        /** {@link FloatingMenuLayout} 的布局参数 */
        private ViewGroup.LayoutParams mLayoutParams;

        private MenuStateChangeListener mMenuStateChangeListener;

        /**
         * @param parent         {@link FloatingMenuLayout} 的父控件，建议{@link FrameLayout}
         * @param menuActionView 菜单入口 View，可拖拽
         */
        public Builder(ViewGroup parent, View menuActionView) {
            mParent = parent;
            mMenuActionView = menuActionView;
        }

        public Builder setMenu(View... view) {
            mMenuViewArray = view;
            return this;
        }

        /** 设置菜单 item 间距值，单位 dip */
        public Builder setMenuItemMargin(int marginDip) {
            mMenuMarginDip = marginDip;
            return this;
        }

        /** 设置菜单收缩、展开动画执行时间 单位 ms */
        public Builder setAnimationDuration(int duration) {
            mDuration = duration;
            return this;
        }

        /**
         * 设置 {@link FloatingMenuLayout} 的父控件，也是浮动拖拽区域
         *
         * @param parent {@link FloatingMenuLayout} 的父控件，建议{@link FrameLayout}
         * @param lp     {@link FloatingMenuLayout} 的布局参数
         */
        public Builder setFloatParent(ViewGroup parent, ViewGroup.LayoutParams lp) {
            mParent = parent;
            mLayoutParams = lp;
            return this;
        }

        /** 设置菜单入口初始化位置，默认是 右边中间 */
        public Builder setMenuInitGravity(int gravity) {
            mMenuActionViewInitGravity = gravity;
            return this;
        }

        /**
         * 设置菜单状态变化监听
         */
        public Builder setMenuStatChangeListener(MenuStateChangeListener listener) {
            mMenuStateChangeListener = listener;
            return this;
        }

        public FloatingMenuLayout build(Context context) {
            FloatingMenuLayout floatingMenu = new FloatingMenuLayout(context);
            floatingMenu.mMenuMargin = dip2px(context, mMenuMarginDip);
            floatingMenu.mDuration = mDuration;
            floatingMenu.mMenuStateChangeListener = mMenuStateChangeListener;

            if (mLayoutParams != null) {
                floatingMenu.setLayoutParams(mLayoutParams);
            }

            // 设置 menuActionView 位置
            LayoutParams menuActionLp = (LayoutParams) mMenuActionView.getLayoutParams();
            if (menuActionLp == null) {
                menuActionLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            menuActionLp.gravity = mMenuActionViewInitGravity;

            floatingMenu.setMenuViewArray(mMenuViewArray);
            floatingMenu.setMenuActionView(mMenuActionView);

            mParent.addView(floatingMenu);

            return floatingMenu;
        }

        private int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        @TargetApi(19)
        public LayoutParams(@NonNull LayoutParams source) {
            super(source);
        }
    }
}
