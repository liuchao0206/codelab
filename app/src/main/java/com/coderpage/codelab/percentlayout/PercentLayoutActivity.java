package com.coderpage.codelab.percentlayout;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.coderpage.codelab.codelab.R;


public class PercentLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percent_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_percentlayout);
        setSupportActionBar(toolbar);
    }
}
