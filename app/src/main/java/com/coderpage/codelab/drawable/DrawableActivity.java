package com.coderpage.codelab.drawable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.coderpage.codelab.BaseActivity;
import com.coderpage.codelab.codelab.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.coderpage.codelab.utils.LogUtils.LOGE;
import static com.coderpage.codelab.utils.LogUtils.makeLogTag;


public class DrawableActivity extends BaseActivity {
    private static final String TAG = makeLogTag(DrawableActivity.class);

    @BindView(R.id.iv)
    ImageView mImageView;
    @BindView(R.id.ivMatrix)
    ImageView mImageViewMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable);
        setTitle("Drawable");
        ButterKnife.bind(this);
        showBigSizePhoto();
        showMatrixPhoto();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsBack((view) -> {
            finish();
        });
    }

    public void openPhotoActivity(View view) {
        startActivity(new Intent(this, PhotoActivity.class));
    }

    private void showBigSizePhoto() {
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


    //    Canvas canvas;
    Matrix matrix;
    Bitmap bitmap;

    private void showMatrixPhoto() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.zuozhu, options);
        options.inSampleSize = calculateInSampleSize(options, 100, 100);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zuozhu, options)
                .copy(Bitmap.Config.ARGB_8888, true);
        matrix = new Matrix();
        mImageViewMatrix.setImageBitmap(bitmap);
    }

    @OnClick({R.id.btnScale, R.id.btnInverted})
    public void onMatrix(Button button) {
        switch (button.getId()) {
            case R.id.btnScale:
                if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) return;
                matrix.postScale(0.5f, 0.5f);
                Bitmap bp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                bitmap = bp;
                mImageViewMatrix.setImageBitmap(bitmap);
                break;
            case R.id.btnInverted:
                matrix.setScale(1, -1);
                matrix.postTranslate(0, bitmap.getHeight());
                bp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                bitmap = bp;
                mImageViewMatrix.setImageBitmap(bitmap);
                break;
        }
    }
}
