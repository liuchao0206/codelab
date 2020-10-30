package com.coderpage.codelab.drawable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

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
    @BindView(R.id.iv_cover)
    ImageView mImageViewCover;
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

        generateBlurBitmap();
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
        BitmapFactory.decodeStream(getResources().openRawResource(R.raw.photo2), null, options);
        options.inSampleSize = calculateInSampleSize(options, 100, 100);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(
                getResources().openRawResource(R.raw.photo2), null, options);
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

            default:
                break;
        }
    }

    public void generateBlurBitmap() {

        int srcColor = Color.parseColor("#F2F2F6");
        int mixColor = Color.parseColor("#F2F2F6");

        srcColor = getResources().getColor(R.color.colorBlur);
        mixColor = getResources().getColor(R.color.colorBlurMix);

        int[] colorArray = new int[100 * 100];
        for (int i = 0; i < colorArray.length; i++) {
            colorArray[i] = i % 4 == 0 ? mixColor:srcColor;
        }

        Bitmap src = Bitmap.createBitmap(colorArray, 100,100, Bitmap.Config.ARGB_8888);
        Bitmap blurBitmap = blur(this, src, 1, 10);
        mImageViewCover.setImageBitmap(blurBitmap);
    }

    private Bitmap blur(Context context, Bitmap bitmap, float bitmapScale, int blurRadius) {
        //先对图片进行压缩然后再blur 缩短时间
        Bitmap inputBitmap = bitmap;
        if (bitmapScale < 1) {
            inputBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * bitmapScale),
                    Math.round(bitmap.getHeight() * bitmapScale), false);
        }
        Bitmap outputBitmap;
        //创建空的Bitmap用于输出
        outputBitmap = Bitmap.createBitmap(inputBitmap);
        //初始化Renderscript
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //native层分配内存空间
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        //设置blur的半径然后进行blur
        theIntrinsic.setRadius(blurRadius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        //拷贝blur后的数据到java缓冲区中
        tmpOut.copyTo(outputBitmap);
        //销毁Renderscript
        rs.destroy();

        return outputBitmap;
    }
}
