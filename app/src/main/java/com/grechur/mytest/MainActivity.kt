package com.grechur.mytest

import android.os.Bundle
import android.view.View
import com.grechur.base.BaseActivity
import com.grechur.base.Router
import com.grechur.buried_annotation.BuriedMethod
import com.grechur.route_annotation.ZRoute

@ZRoute(path = "/app/mainPage")
class MainActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        pageParams.put("page","main")
    }

    override fun initView() {

    }

    @BuriedMethod(click = Constants.FOOD_CLICK)
    fun food(view: View) {
        clickName = "foodClick"
        Router.getInstance().build("/food/food").navigation(this);
    }

    @BuriedMethod(click = Constants.WAIMAI_CLICK)
    fun waimai(view: View) {
        clickName = "waimaiClick"
        Router.getInstance().build("/waimai/waimai").navigation(this);
    }

}