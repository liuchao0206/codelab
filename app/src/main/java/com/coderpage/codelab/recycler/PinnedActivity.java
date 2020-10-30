package com.coderpage.codelab.recycler;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coderpage.codelab.BaseActivity;
import com.coderpage.codelab.codelab.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author lc. 2020-10-30 15:58
 * @since 1.0.0)
 */
public class PinnedActivity extends BaseActivity {

    private View mPinnedView;

    private ItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinned);

        mPinnedView = findViewById(R.id.ly_pinned_in_activity);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new ItemAdapter();
        recyclerView.setAdapter(mAdapter);

        GridLayoutManager lm = new GridLayoutManager(this, 5);
        lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mAdapter.getItemViewType(position);
                return type == Item.TYPE_PINNED ? 5 : 1;
            }
        });
        recyclerView.setLayoutManager(lm);

        List<Item> dataList = new ArrayList<>(101);
        for (int i = 0; i < 60; i++) {
            dataList.add(new Item(Item.TYPE_ITEM));
        }
        dataList.add(new Item(Item.TYPE_PINNED));
        for (int i = 0; i < 60; i++) {
            dataList.add(new Item(Item.TYPE_ITEM));
        }
        mAdapter.setDataList(dataList);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int[] location = new int[2];

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                View pinnedView = recyclerView.findViewById(R.id.ly_pinned);
                if (pinnedView == null) {
                    return;
                }
                pinnedView.getLocationInWindow(location);
                int y = location[1];
                mPinnedView.getLocationInWindow(location);
                int targetY = location[1];

                Log.i("Pinned","y:" + y + " targetY:" + targetY);

                if (y <= targetY) {
                    mPinnedView.setVisibility(View.GONE);
                } else {
                    mPinnedView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Item> mDataList = new ArrayList<>();

        void setDataList(List<Item> list) {
            mDataList.clear();
            if (list != null) {
                mDataList.addAll(list);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return mDataList.get(position).getType();
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == Item.TYPE_PINNED) {
                return new ItemPinnedViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_pinned_item_pinned, parent, false));
            }
            return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_pinned_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ItemViewHolder(View view) {
            super(view);
        }
    }

    class ItemPinnedViewHolder extends RecyclerView.ViewHolder {
        ItemPinnedViewHolder(View view) {
            super(view);
        }
    }

    private static class Item {
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_PINNED = 1;
        private int type;

        Item(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
