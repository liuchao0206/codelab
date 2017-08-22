package com.coderpage.codelab.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.coderpage.codelab.BaseActivity;
import com.coderpage.codelab.codelab.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.coderpage.codelab.utils.LogUtils.LOGE;
import static com.coderpage.codelab.utils.LogUtils.makeLogTag;


public class DrawableActivity extends BaseActivity {
    private static final String TAG = makeLogTag(DrawableActivity.class);

    @BindView(R.id.iv)
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable);
        setTitle("Drawable");
        ButterKnife.bind(this);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsBack((view) -> {
            finish();
        });
        showPhoto();
    }

    private void showPhoto() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getResources().openRawResource(R.raw.photo), null, options);
        options.inSampleSize = calculateInSampleSize(options, 100, 100);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(
                getResources().openRawResource(R.raw.photo), null, options);
        mImageView.setImageBitmap(bitmap);
    }

    /**
     * 参考 http://blog.csdn.net/guolin_blog/article/details/9316683
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int width, int height) {
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;
        LOGE(TAG, "bitmap width=" + bitmapWidth + "  bitmap height=" + bitmapHeight);
        int inSampleSize = 1;
        if (bitmapWidth > width || bitmapHeight > height) {
            int heightRatio = Math.round(bitmapHeight / height);
            int widthRatio = Math.round(bitmapWidth / width);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        return inSampleSize;
    }
}
