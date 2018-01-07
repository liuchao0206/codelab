package com.coderpage.codelab.widget;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coderpage.codelab.codelab.R;
import com.coderpage.codelab.widget.floating.FloatViewService;
import com.coderpage.codelab.widget.floating.FloatingMenuLayout;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WidgetActivity extends AppCompatActivity {

    @BindView(R.id.rpb)
    RoundPercentBar mRpb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_widget);
        setSupportActionBar(toolbar);

        mRpb.setPercent(0.6f);
        mRpb.setText("餐饮");

        FrameLayout floatParent = (FrameLayout) getWindow().getDecorView();

        final String tagBack = "back";
        final String tagClose = "close";

        FrameLayout menuActionView = new FrameLayout(this);
        menuActionView.setBackgroundResource(R.drawable.bg_floating_menu);
        menuActionView.setLayoutParams(new FloatingMenuLayout.LayoutParams(dip2px(40), dip2px(40)));

        ImageView menuActionViewImage = new ImageView(this);
        menuActionViewImage.setBackgroundResource(R.drawable.ic_ring);
        FrameLayout.LayoutParams menuActionViewImageLp = new FrameLayout.LayoutParams(dip2px(30), dip2px(30));
        menuActionViewImageLp.gravity = Gravity.CENTER;
        menuActionViewImage.setLayoutParams(menuActionViewImageLp);

        menuActionView.addView(menuActionViewImage);


        TextView menuBack = new TextView(this);
        menuBack.setText("返回");
        menuBack.setTextColor(Color.WHITE);
        menuBack.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        menuBack.setBackgroundResource(R.drawable.bg_floating_menu);
        menuBack.setLayoutParams(new FrameLayout.LayoutParams(dip2px(40), dip2px(40)));
        menuBack.setTag(tagBack);
        menuBack.setGravity(Gravity.CENTER);

        TextView menuClose = new TextView(this);
        menuClose.setText("关闭");
        menuClose.setTextColor(Color.WHITE);
        menuClose.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        menuClose.setBackgroundResource(R.drawable.bg_floating_menu);
        menuClose.setLayoutParams(new FrameLayout.LayoutParams(dip2px(40), dip2px(40)));
        menuClose.setGravity(Gravity.CENTER);
        menuClose.setTag(tagClose);

        new FloatingMenuLayout
                .Builder(floatParent, menuActionView)
                .setMenu(menuBack, menuClose)
                .setMenuStatChangeListener(new FloatingMenuLayout.MenuActionListener() {
                    @Override
                    public void onStateChange(FloatingMenuLayout layout, View menuActionView, int currentState) {
                        if (currentState == FloatingMenuLayout.MENU_EXPAND) {
                            menuActionViewImage.setBackgroundResource(R.drawable.ic_close);
                        } else {
                            menuActionViewImage.setBackgroundResource(R.drawable.ic_ring);
                        }
                    }

                    @Override
                    public void onMenuClick(FloatingMenuLayout layout, View menu) {
                        if (String.valueOf(menu.getTag()).equals(tagBack)) {
                            Toast.makeText(WidgetActivity.this, "返回", Toast.LENGTH_SHORT).show();
                        }
                        if (String.valueOf(menu.getTag()).equals(tagClose)) {
                            Toast.makeText(WidgetActivity.this, "关闭", Toast.LENGTH_SHORT).show();
                        }
                        layout.retractMenus();
                    }
                })
                .build(this);

    }


    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onStart() {
        Intent intent = new Intent(this, FloatViewService.class);
        startService(intent);
        super.onStart();
    }

    @Override
    protected void onStop() {
        Intent intent = new Intent(this, FloatViewService.class);
        stopService(intent);
        super.onStop();
    }
}
