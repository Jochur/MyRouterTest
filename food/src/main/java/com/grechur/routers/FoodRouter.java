package com.grechur.routers;

import android.app.Activity;

import com.grechur.base.IRouterInterface;
import com.grechur.food.FoodActivity;

import java.util.Map;

/**
 * 实现路由接口，将页面通过路由的方式注册到路由表中
 */
public class FoodRouter implements IRouterInterface {
    @Override
    public void loadRouter(Map<String, Class<? extends Activity>> map) {
        map.put("/food/food", FoodActivity.class);
    }
}
