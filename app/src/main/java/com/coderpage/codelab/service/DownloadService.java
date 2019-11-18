package com.coderpage.codelab.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

import static com.coderpage.codelab.utils.LogUtils.LOGE;
import static com.coderpage.codelab.utils.LogUtils.makeLogTag;

/**
 * @author lc. 2017-08-20
 * @since 0.1.0
 */

public class DownloadService extends Service {
    private static final String TAG = makeLogTag(DownloadService.class);

    private static final int MAX_PROGRESS = 100;
    private volatile int progress = 0;
    private ProgressListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LOGE(TAG, "on bind");
        return new DownloadBinder();
    }

    public void starDownload(ProgressListener listener) {
        mListener = listener;
        new Thread(() -> {
            while (progress < MAX_PROGRESS) {
                progress = progress + 5;
                if (mListener != null) {
                    mListener.onProgress(progress);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

}
