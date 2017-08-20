package com.coderpage.codelab.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.coderpage.codelab.codelab.R;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ServiceActivity extends AppCompatActivity {

    @BindView(R.id.tvProgress)
    TextView progressTv;

    MServiceConnection mServiceConnection;
    boolean isBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_service));
        setTitle("service");
        ButterKnife.bind(this);
    }

    public void startDownload(View view) {
        Intent intent = new Intent(this, DownloadService.class);
        mServiceConnection = new MServiceConnection();
        isBind = bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mServiceConnection != null && isBind) {
            unbindService(mServiceConnection);
            isBind = false;
        }
        super.onDestroy();
    }

    private class MServiceConnection implements ServiceConnection {

        WeakReference<TextView> mProgressTvRef;

        MServiceConnection() {
            mProgressTvRef = new WeakReference<>(progressTv);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService downloadService = ((DownloadService.DownloadBinder) service).getService();
            downloadService.starDownload((progress) -> {
                TextView textView = mProgressTvRef.get();
                if (textView != null) {
                    textView.post(() -> {
                        textView.setText(progress + "%s");
                    });
                }
                if (progress >= 100) {
                    unbindService(mServiceConnection);
                    isBind = false;
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
