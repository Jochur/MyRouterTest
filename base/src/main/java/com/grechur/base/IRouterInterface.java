package com.grechur.base;

import android.app.Activity;

import java.util.Map;

/**
 * 通过接口，将组件中的页面class信息注册到路由表
 */
public interface IRouterInterface {

    void loadRouter(Map<String,Class<? extends Activity>> map);

}
