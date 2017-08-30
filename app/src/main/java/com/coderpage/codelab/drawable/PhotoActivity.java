package com.coderpage.codelab.drawable;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.coderpage.codelab.BaseActivity;
import com.coderpage.codelab.codelab.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoActivity extends BaseActivity {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("Photo");
        setToolbarAsClose(v -> finish());
        ButterKnife.bind(this);
        showPhotos();
    }

    private void showPhotos() {
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);

        Cursor cursor = new PhotoCursorLoader(this).loadInBackground();
        List<PhotoItem> photoItemList = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            photoItemList.add(new PhotoItem(cursor));
        }
        cursor.close();

        mRecyclerView.setAdapter(new MAdapter(photoItemList));
        mRecyclerView.setLayoutManager(layoutManager);
    }

    class MAdapter extends RecyclerView.Adapter<MAdapter.MViewHolder> {
        List<PhotoItem> mDataList;
        LayoutInflater mInflater;

        MAdapter(List<PhotoItem> list) {
            mDataList = list;
            mInflater = getLayoutInflater();
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        @Override
        public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MViewHolder(mInflater.inflate(R.layout.recycler_item_photo, parent, false));
        }

        @Override
        public void onBindViewHolder(MViewHolder holder, int position) {
            holder.setData(mDataList.get(position));
        }

        class MViewHolder extends RecyclerView.ViewHolder {
            ImageView mIv;

            MViewHolder(View view) {
                super(view);
                mIv = (ImageView) view.findViewById(R.id.iv);
            }

            void setData(PhotoItem item) {
                Glide.with(PhotoActivity.this).load(item.data).into(mIv);
            }
        }
    }

    class PhotoItem {
        long id;
        String data;
        String displayName;
        String mimeType;
        long size;

        PhotoItem() {
        }

        PhotoItem(Cursor cursor) {
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
            String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE));
            this.id = id;
            this.data = data;
            this.displayName = displayName;
            this.mimeType = mimeType;
            this.size = size;
        }

    }

    static class PhotoCursorLoader extends CursorLoader {
        private static Uri uri = MediaStore.Files.getContentUri("external");
        private static final String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.SIZE,
                "duration"};
        private static final String selection =
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        + " AND "
                        + MediaStore.MediaColumns.SIZE + ">0";
        private static final String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        PhotoCursorLoader(Context context) {
            super(context, uri, projection, selection, null, orderBy);
        }

        @Override
        public Cursor loadInBackground() {
            return super.loadInBackground();
        }
    }
}
