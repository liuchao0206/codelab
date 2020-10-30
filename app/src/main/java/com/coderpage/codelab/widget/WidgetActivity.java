package com.coderpage.codelab.widget;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coderpage.codelab.codelab.R;
import com.coderpage.codelab.widget.datepick.BaseWheelAdapter;
import com.coderpage.codelab.widget.datepick.WheelView;
import com.coderpage.codelab.widget.floating.FloatViewService;
import com.coderpage.codelab.widget.floating.FloatingMenuLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WidgetActivity extends AppCompatActivity {

    @BindView(R.id.rpb)
    RoundPercentBar mRpb;

    @BindView(R.id.wheel_year)
    WheelView mWheelYear;
    @BindView(R.id.wheel_month)
    WheelView mWheelMonth;
    @BindView(R.id.wheel_day)
    WheelView mWheelDay;

    @BindView(R.id.edit_position)
    EditText mPositionEt;

    private BaseWheelAdapter mYearAdapter;
    private BaseWheelAdapter mMonthAdapter;
    private BaseWheelAdapter mDayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar_widget);
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

        initDatePicker();
    }

    private void initDatePicker() {

        final ArrayList<Integer> yearList = new ArrayList<>();
        final ArrayList<Integer> monthList = new ArrayList<>();
        final ArrayList<Integer> dayList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        for (int i = 1992; i <= currentYear; i++) {
            yearList.add(i);
        }
        for (int i = 1; i <= 12; i++) {
            monthList.add(i);
        }
        dayList.addAll(getMonthDays(currentYear, currentMonth + 1));

        mYearAdapter = new BaseWheelAdapter() {

            @Override
            public int getCount() {
                return yearList.size();
            }

            @Override
            public String getText(int position) {
                return String.format(Locale.getDefault(), "%02d", yearList.get(position));
            }
        };
        mMonthAdapter = new BaseWheelAdapter() {

            @Override
            public int getCount() {
                return monthList.size();
            }

            @Override
            public String getText(int position) {
                return String.format(Locale.getDefault(), "%02d", monthList.get(position));
            }
        };
        mDayAdapter = new BaseWheelAdapter() {

            @Override
            public int getCount() {
                return dayList.size();
            }

            @Override
            public String getText(int position) {
                return String.format(Locale.getDefault(), "%02d", dayList.get(position));
            }
        };

        mWheelYear.setListener(position -> {
            Integer year = yearList.get(position);
            Integer month = monthList.get(mWheelMonth.getSelectPosition());
            int dayCount = getMonthDayCount(year, month);
            if (dayCount != dayList.size()) {
                dayList.clear();
                dayList.addAll(getMonthDays(year, month));
                mDayAdapter.notifyDataSetChanged();
            }
        });
        mWheelMonth.setListener(position -> {
            Integer year = yearList.get(mWheelYear.getSelectPosition());
            Integer month = monthList.get(position);
            int dayCount = getMonthDayCount(year, month);
            if (dayCount != dayList.size()) {
                dayList.clear();
                dayList.addAll(getMonthDays(year, month));
                mDayAdapter.notifyDataSetChanged();
            }
        });

        mWheelYear.setAdapter(mYearAdapter);
        mWheelMonth.setAdapter(mMonthAdapter);
        mWheelDay.setAdapter(mDayAdapter);

        mWheelYear.setSelectPosition(yearList.size() - 1);
        mWheelMonth.setSelectPosition(currentMonth);
        mWheelDay.setSelectPosition(currentDay - 1);
    }

    /**
     * @param year  年
     * @param month 月（1-12）
     */
    private List<Integer> getMonthDays(int year, int month) {
        int daysCount = getMonthDayCount(year, month);
        List<Integer> daysList = new ArrayList<>(daysCount);
        for (int i = 1; i <= daysCount; i++) {
            daysList.add(i);
        }
        return daysList;
    }

    private int getMonthDayCount(int year, int month) {
        int daysCount = 30;
        if (month == 1
                || month == 3
                || month == 5
                || month == 7
                || month == 8
                || month == 10
                || month == 12) {
            daysCount = 31;
        }
        if (month == 2) {
            daysCount = year % 4 == 0 ? 29 : 28;
        }
        return daysCount;
    }

    @OnClick(R.id.button_set_position)
    public void onSetPositionClick(View view) {
        mWheelYear.setSelectPosition(string2Int(mPositionEt.getText().toString()));
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int string2Int(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    protected void onStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(this, FloatViewService.class);
                startService(intent);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            }
        } else {
            Intent intent = new Intent(this, FloatViewService.class);
            startService(intent);
        }
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getApplicationContext())) {
                    Intent intent = new Intent(this, FloatViewService.class);
                    startService(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "拒绝悬浮窗权限", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        Intent intent = new Intent(this, FloatViewService.class);
        stopService(intent);
        super.onStop();
    }
}
