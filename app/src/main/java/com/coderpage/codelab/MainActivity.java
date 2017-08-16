package com.coderpage.codelab;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coderpage.codelab.codelab.R;
import com.coderpage.codelab.percentlayout.PercentLayoutActivity;
import com.coderpage.codelab.widget.WidgetActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
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

        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new MAdapter());
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
                }
            }
        }
    }

    private enum Item {
        WIDGETS("widgets"),
        PERCENT_LAYOUT("percentLayout");

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
}
