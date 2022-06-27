package com.grechur.base;

import com.grechur.base.template.IRouteGroup;
import com.grechur.base.template.IService;
import com.grechur.route_annotation.RouteMeta;

import java.util.HashMap;
import java.util.Map;

public class Warehouse {
    // root 映射表 保存分组信息
    static Map<String,Class<? extends IRouteGroup>> groupIndex = new HashMap<>();

    // group 映射表 保存组中的所有数据
    static Map<String, RouteMeta> routes = new HashMap<>();

    //group 映射表 保存组中的所有数据
    static Map<Class, IService> services = new HashMap();
}
