package com.grechur.base.template;



import com.grechur.route_annotation.RouteMeta;

import java.util.Map;

public interface IRouteGroup {

    void loadInto(Map<String, RouteMeta> atlas);
}
