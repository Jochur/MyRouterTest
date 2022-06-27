package com.grechur.waimai;

import android.view.View;

import com.grechur.base.BaseActivity;
import com.grechur.base.Router;
import com.grechur.buried_annotation.BuriedMethod;
import com.grechur.route_annotation.ZRoute;

import java.util.HashMap;
import java.util.Map;

@ZRoute(path = "/waimai/waimai")
public class WaimaiActivity extends BaseActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_waimai);
//        pageParams.put("page","waimai");
//    }


    @Override
    public int setLayout() {
        return R.layout.activity_waimai;
    }

    @Override
    public void initData() {
        pageParams.put("page","waimai");
    }

    @Override
    public void initView() {

    }

    @BuriedMethod(click = Constants.WAIMAI_CLICK)
    public void food(View view) {
        clickName = "waimaiClick";
        Router.getInstance().build("/food/food").navigation(this);
    }

}