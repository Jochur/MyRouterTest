package com.grechur.routers;

import android.app.Activity;

import com.grechur.base.IRouterInterface;
import com.grechur.waimai.WaimaiActivity;

import java.util.Map;

/**
 * 实现路由接口，将页面通过路由的方式注册到路由表中
 */
public class WaimaiRouter implements IRouterInterface {
    @Override
    public void loadRouter(Map<String, Class<? extends Activity>> map) {
        map.put("/waimai/waimai", WaimaiActivity.class);
    }
}
