package com.coderpage.codelab.widget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

//        FloatingMenu1 floatingMenu = new FloatingMenu1(this);
////        floatingMenu.setParent(floatParent);
//        floatingMenu.setFloatBounds(0, 0, 1080, 1920);
//        floatingMenu.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//


        ImageView menuActionView = new ImageView(this);
        menuActionView.setBackgroundResource(R.drawable.page_loading_02);
        menuActionView.setLayoutParams(new FloatingMenuLayout.LayoutParams(180, 180));


        ImageView menuView = new ImageView(this);
        menuView.setBackgroundResource(R.drawable.page_loading_02);
        menuView.setLayoutParams(new FloatingMenuLayout.LayoutParams(150, 150));


        ImageView menuView1 = new ImageView(this);
        menuView1.setBackgroundResource(R.drawable.page_loading_02);
        menuView1.setLayoutParams(new FloatingMenuLayout.LayoutParams(150, 150));

        FloatingMenuLayout floatingMenuLayout = new FloatingMenuLayout
                .Builder(floatParent, menuActionView)
                .setMenu(menuView, menuView1)
                .setMenuStatChangeListener(new FloatingMenuLayout.MenuStateChangeListener() {
                    @Override
                    public void onStateChange(FloatingMenuLayout layout, View menuActionView, int currentState) {
                        if (currentState == FloatingMenuLayout.MENU_EXPAND) {
                            menuActionView.setBackgroundResource(R.drawable.page_loading_01);
                        } else {
                            menuActionView.setBackgroundResource(R.drawable.page_loading_02);
                        }
                    }
                })
                .build(this);

        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingMenuLayout.retractMenus();
                Toast.makeText(WidgetActivity.this, "click 1", Toast.LENGTH_SHORT).show();
            }
        });

        menuView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingMenuLayout.retractMenus();
                Toast.makeText(WidgetActivity.this, "click 2", Toast.LENGTH_SHORT).show();
            }
        });

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
