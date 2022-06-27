package com.grechur.food;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.grechur.base.BaseActivity;
import com.grechur.base.Router;
import com.grechur.buried_annotation.BuriedMethod;
import com.grechur.route_annotation.ZRoute;

import java.util.HashMap;
import java.util.Map;

@ZRoute(path = "/food/food")
public class FoodActivity extends BaseActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_food);
//        pageParams.put("page","food");
//    }


    @Override
    public int setLayout() {
        return R.layout.activity_food;
    }

    @Override
    public void initData() {
        pageParams.put("page","food");
    }

    @Override
    public void initView() {

    }

    @BuriedMethod(click = Constants.FOOD_CLICK)
    public void toWaimai(View view) {
        clickName = "foodClick";
        Router.getInstance().build("/waimai/waimai").navigation(this);
    }
}