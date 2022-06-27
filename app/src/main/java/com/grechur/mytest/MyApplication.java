package com.grechur.mytest;

import android.app.Application;

import com.grechur.base.Router;
import com.grechur.route_annotation.ZRoute;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.getInstance().init(this);
    }
}
