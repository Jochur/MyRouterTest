package com.grechur.base;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class TraceUtils {
    public static void onActivityCreate(Activity activity) {
        Map<String, Object> map = new HashMap<>();
        long timeMillis = System.currentTimeMillis();
        map.put("startTime",timeMillis);
        onActivityCreate(activity, map);
    }

    public static void onActivityCreate(Activity activity, Map<String, Object> pageParams) {
        long timeMillis = System.currentTimeMillis();
        pageParams.put("startTime",timeMillis);
        Log.e("zhouzhu", activity.getClass().getName() + "onActivityCreate" + " pageParams:" + pageParams.get("page"));
    }

    public static void onActivityDestroy(Activity activity) {
        Map<String, Object> map = new HashMap<>();
        onActivityDestroy(activity, map);
    }

    public static void onActivityDestroy(Activity activity, Map<String, Object> pageParams) {
        long timeMillis = System.currentTimeMillis();
        pageParams.put("endTime",timeMillis);
        Log.e("zhouzhu", activity.getClass().getName() + "onActivityDestroy" + " pageParams:" + pageParams.get("page"));
    }

    public static void onClickMethod(String method) {
        Map<String, Object> map = new HashMap<>();
        map.put("method", method);
        onClickMethod(method, map);
    }

    public static void onClickMethod(String method, Map<String, Object> methodParams) {
        methodParams.put("method", method);
        Log.e("zhouzhu", method + "click" + " methodParams:" + methodParams.get("method"));

    }
}
