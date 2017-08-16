package com.coderpage.codelab.widget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.coderpage.codelab.codelab.R;

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
    }
}
