package com.coderpage.codelab.animation;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.coderpage.codelab.BaseActivity;
import com.coderpage.codelab.codelab.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 参考代码 http://www.jianshu.com/p/420629118c10
 */
public class AnimationActivity extends BaseActivity {

    @BindView(R.id.ivLoading)
    ImageView mLoadingIv;
    @BindView(R.id.viewTween)
    View mAnimatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        setTitle("Animation");
        setToolbarAsBack(v -> finish());
        ButterKnife.bind(this);

        showLoadingImage();
    }

    private void showLoadingImage() {
        mLoadingIv.setImageResource(R.drawable.frame_anim_1);
        AnimationDrawable drawable = (AnimationDrawable) mLoadingIv.getDrawable();
        drawable.setOneShot(false);
        drawable.start();
    }

    @OnClick({R.id.alpha, R.id.rotate, R.id.translate, R.id.scale})
    public void tweenAnimation(Button button) {
        int id = button.getId();
        switch (id) {
            case R.id.alpha:
                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                alphaAnimation.setDuration(2000);
                alphaAnimation.setInterpolator(new LinearInterpolator());
                mAnimatorView.startAnimation(alphaAnimation);
                break;
            case R.id.rotate:
                RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(1000);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new LinearInterpolator());
                mAnimatorView.startAnimation(rotateAnimation);
                break;
            case R.id.translate:
                TranslateAnimation translateAnimation = new TranslateAnimation(0, -200, 0, 0);
                translateAnimation.setDuration(2000);
                translateAnimation.setInterpolator(new LinearInterpolator());
                mAnimatorView.startAnimation(translateAnimation);
                break;
            case R.id.scale:
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.5f, 0.0f, 1.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(2000);
                scaleAnimation.setInterpolator(new LinearInterpolator());
                mAnimatorView.startAnimation(scaleAnimation);
                break;
        }

    }

    @OnClick(R.id.valueAnimator)
    public void showValueAnimator(Button button) {
        ObjectAnimator alphaAnimator = new ObjectAnimator();
        alphaAnimator.setDuration(2000);
        alphaAnimator.setPropertyName("alpha");
        alphaAnimator.setFloatValues(0f, 1.0f);
        alphaAnimator.setTarget(mAnimatorView);
        alphaAnimator.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator translateAnimator = new ObjectAnimator();
        translateAnimator.setDuration(2000);
        translateAnimator.setPropertyName("x");
        translateAnimator.setFloatValues(mAnimatorView.getX(),
                mAnimatorView.getX() - 200f, mAnimatorView.getX());
        translateAnimator.setTarget(mAnimatorView);
        translateAnimator.setInterpolator(new LinearInterpolator());

        ObjectAnimator scaleXAnimator = new ObjectAnimator();
        scaleXAnimator.setDuration(2000);
        scaleXAnimator.setPropertyName("scaleX");
        scaleXAnimator.setFloatValues(1f, 0, 1.5f, 1f);
        scaleXAnimator.setTarget(mAnimatorView);
        scaleXAnimator.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator scaleYAnimator = new ObjectAnimator();
        scaleYAnimator.setDuration(2000);
        scaleYAnimator.setPropertyName("scaleY");
        scaleYAnimator.setFloatValues(1f, 0, 1.5f, 1f);
        scaleYAnimator.setTarget(mAnimatorView);
        scaleYAnimator.setInterpolator(new DecelerateInterpolator());

        AnimatorSet scaleAnimator = new AnimatorSet();
        scaleAnimator.play(scaleXAnimator).with(scaleYAnimator);

        ObjectAnimator turnOverXAnimator = new ObjectAnimator();
        turnOverXAnimator.setTarget(mAnimatorView);
        turnOverXAnimator.setPropertyName("rotationX");
        turnOverXAnimator.setFloatValues(180f, 0);

        ObjectAnimator turnOverYAnimator = new ObjectAnimator();
        turnOverYAnimator.setTarget(mAnimatorView);
        turnOverYAnimator.setPropertyName("rotationY");
        turnOverYAnimator.setFloatValues(180f, 0);

        ObjectAnimator rotateAnimator = new ObjectAnimator();
        rotateAnimator.setPropertyName("rotation");
        rotateAnimator.setDuration(1000);
        rotateAnimator.setFloatValues(0, 360);
        rotateAnimator.setTarget(mAnimatorView);

        ObjectAnimator colorAnimator = new ObjectAnimator();
        colorAnimator.setTarget(mAnimatorView);
        colorAnimator.setDuration(3000);
        colorAnimator.setPropertyName("backgroundColor");
        colorAnimator.setInterpolator(new DecelerateInterpolator());
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.setIntValues(
                getResources().getColor(R.color.black_alpha_02),
                getResources().getColor(R.color.black_alpha_03),
                getResources().getColor(R.color.black_alpha_08),
                getResources().getColor(R.color.black_alpha_12),
                getResources().getColor(R.color.black_alpha_20),
                getResources().getColor(R.color.black_alpha_25),
                getResources().getColor(R.color.black_alpha_30),
                getResources().getColor(R.color.black_alpha_35),
                getResources().getColor(R.color.black_alpha_50),
                getResources().getColor(R.color.black_alpha_70),
                getResources().getColor(R.color.colorAccent));


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setTarget(mAnimatorView);
        animatorSet.play(alphaAnimator).before(translateAnimator);
        animatorSet.play(translateAnimator).before(scaleAnimator);
        animatorSet.play(scaleAnimator).before(turnOverXAnimator);
        animatorSet.play(turnOverXAnimator).before(turnOverYAnimator);
        animatorSet.play(turnOverYAnimator).before(rotateAnimator);
        animatorSet.play(rotateAnimator).before(colorAnimator);

        animatorSet.start();
    }
}
