package com.grechur.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity {
    protected Map<String, Object> pageParams = new HashMap();
    protected Map<String, Object> methodParams = new HashMap();
    protected String clickName = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        initData();
        initView();
    }

    public abstract int setLayout();
    public abstract void initData();
    public abstract void initView();

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
