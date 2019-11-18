package com.coderpage.codelab;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.coderpage.codelab.animation.AnimationActivity;
import com.coderpage.codelab.ble.BleActivity;
import com.coderpage.codelab.codelab.R;
import com.coderpage.codelab.drawable.DrawableActivity;
import com.coderpage.codelab.fingerprint.FingerPrintActivity;
import com.coderpage.codelab.jni.JniActivity;
import com.coderpage.codelab.percentlayout.PercentLayoutActivity;
import com.coderpage.codelab.search.SearchActivity;
import com.coderpage.codelab.service.ServiceActivity;
import com.coderpage.codelab.widget.WidgetActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.coderpage.codelab.utils.LogUtils.LOGE;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new MAdapter());

        LOGE(TAG, "on create..");

        Toast.makeText(this, "百度渠道", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        LOGE(TAG, "on create with persistent state..");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LOGE(TAG, "on onRestart..");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LOGE(TAG, "on onStart..");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LOGE(TAG, "on onResume..");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LOGE(TAG, "on onPause..");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LOGE(TAG, "on onStop..");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LOGE(TAG, "on onDestroy..");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LOGE(TAG, "on configuration changed..");
        LOGE(TAG, "当前屏幕状态= "
                + (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "横屏" : "竖屏"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_search:
                Intent queryIntent = new Intent(this, SearchActivity.class);
                startActivity(queryIntent);
                break;
        }
        return false;
    }

    private class MAdapter extends RecyclerView.Adapter<MAdapter.MViewHolder> {

        private Item[] mItems = Item.values();

        @Override
        public int getItemCount() {
            return mItems.length;
        }

        @Override
        public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MViewHolder(
                    getLayoutInflater().inflate(R.layout.recycle_item_main_act, parent, false));
        }

        @Override
        public void onBindViewHolder(MViewHolder holder, int position) {
            holder.setItem(mItems[position]);
        }

        class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView mNameTv;

            Item item;

            MViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                mNameTv = (TextView) view.findViewById(R.id.tv);
            }

            void setItem(Item item) {
                this.item = item;
                mNameTv.setText(item.getName());
            }

            @Override
            public void onClick(View view) {
                switch (item) {
                    case WIDGETS:
                        startActivity(new Intent(MainActivity.this, WidgetActivity.class));
                        break;
                    case PERCENT_LAYOUT:
                        startActivity(new Intent(MainActivity.this, PercentLayoutActivity.class));
                        break;
                    case SERVICES:
                        startActivity(new Intent(MainActivity.this, ServiceActivity.class));
                        break;
                    case DRAWABLE:
                        startActivity(new Intent(MainActivity.this, DrawableActivity.class));
                        break;
                    case ANIMATION:
                        startActivity(new Intent(MainActivity.this, AnimationActivity.class));
                        break;
                    case JNI:
                        startActivity(new Intent(MainActivity.this, JniActivity.class));
                        break;
                    case BLE:
                        startActivity(new Intent(MainActivity.this, BleActivity.class));
                        break;
                    case FINGERPRINT:
                        startActivity(new Intent(MainActivity.this, FingerPrintActivity.class));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private enum Item {
        WIDGETS("widgets"),
        PERCENT_LAYOUT("percentLayout"),
        SERVICES("service"),
        DRAWABLE("drawable"),
        ANIMATION("animation"),
        JNI("jni"),
        BLE("bluetooth"),
        FINGERPRINT("fingerprint"),
        ;

        private String name;

        Item(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public void request(View v) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        String[] strs = {"name1", "name2", "name3"};
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("yoyo");
                Log.i(TAG, "subscribe: " + Thread.currentThread().getName());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.i(TAG, "accept: " + s + "  " + Thread.currentThread().getName());
                    }
                });
    }

    @OnClick(R.id.ivMenu)
    public void openBottomMenu(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this,
                R.style.BottomSheet);
        View contentView = getLayoutInflater().inflate(R.layout.layout_bottom_menu, null);
        contentView.findViewById(R.id.lyAbout).setOnClickListener((v) -> {
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setContentView(contentView);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        configWindow(bottomSheetDialog.getWindow(), getResources().getColor(R.color.colorAccent));
    }

    private void configWindow(Window window, int color) {
        if (window == null) {
            return;
        }
        window.setWindowAnimations(R.style.BottomSheetAnimation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }
}
