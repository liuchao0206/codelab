package com.coderpage.codelab.fingerprint;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.coderpage.codelab.codelab.R;

/**
 * @author lc. 2019-06-14 18:59
 * @since 0.6.2
 */
public class FingerPrintActivity extends AppCompatActivity {

    private TextView mLogText;

    private FingerprintManagerCompat mFingerprintManager;
    private CancellationSignal mCancellationSignal;
    private CryptoObjectHelper mCryptoObjectHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        mFingerprintManager = FingerprintManagerCompat.from(this);
        mCancellationSignal = new CancellationSignal();
        mCryptoObjectHelper = new CryptoObjectHelper();

        mLogText = (TextView) findViewById(R.id.tv_text);
        findViewById(R.id.button_start).setOnClickListener(v -> startFingerPrintAuth());
        findViewById(R.id.button_cancel).setOnClickListener(v -> mCancellationSignal.cancel());
    }

    @TargetApi(23)
    private void cancelFingerPrintAuth() {
        mCancellationSignal.cancel();
    }

    @TargetApi(23)
    private void startFingerPrintAuth() {

        log("start.");
        boolean hardwareDetected = mFingerprintManager.isHardwareDetected();
        if (!hardwareDetected) {
            log("err: 无指纹识别");
            Toast.makeText(this, "无指纹识别", Toast.LENGTH_SHORT).show();
            return;
        }

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager == null || !keyguardManager.isKeyguardSecure()) {
            log("err: 没有设置锁屏");
            Toast.makeText(this, "需要先设置锁屏", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mFingerprintManager.hasEnrolledFingerprints()) {
            log("err: 没有指纹");
            Toast.makeText(this, "需要先添加指纹", Toast.LENGTH_SHORT).show();
            return;
        }

        mFingerprintManager.authenticate(mCryptoObjectHelper.buildCryptoObject(), 0, mCancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                super.onAuthenticationError(errMsgId, errString);
                log("err: " + errMsgId + " " + errString);
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                super.onAuthenticationHelp(helpMsgId, helpString);
                log("help: " + helpMsgId + " " + helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                log("认证成功");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                log("认证失败");
            }
        }, null);
    }

    private void log(String message) {
        mLogText.append(message + "\n");
    }
}
