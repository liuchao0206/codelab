package com.coderpage.codelab.jni;

import android.os.Bundle;
import android.widget.TextView;

import com.coderpage.codelab.BaseActivity;
import com.coderpage.codelab.codelab.R;
//import com.coderpage.libjni.NativeInterface;

import butterknife.BindView;
import butterknife.ButterKnife;


public class JniActivity extends BaseActivity {

    @BindView(R.id.tv)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni);
        setTitle("JNI");
        setToolbarAsBack(v -> finish());
        ButterKnife.bind(this);

        //tv.setText(new NativeInterface().stringFromJni());
        //tv.append("JNI Add 1+1=" + new NativeInterface().add(1, 1));
    }
}
