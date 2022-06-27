package com.grechur.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.grechur.base.template.IRouteGroup;
import com.grechur.base.template.IRouteRoot;
import com.grechur.base.template.IService;
import com.grechur.route_annotation.RouteMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Router {
    private static final String TAG = "Router";
    private static final String ROUTE_ROOT_PAKCAGE = "com.grechur.base";
    private static final String SDK_NAME = "ZRouter";
    private static final String SEPARATOR = "$$";
    private static final String SUFFIX_ROOT = "Root";
    private static Router instance = null;
    //    private Map<String,Class<? extends Activity>> map = new HashMap<>();
    private Context mContext;

    private Router() {

    }

    public static Router getInstance() {
        if (instance == null) {
            synchronized (Router.class) {
                if (instance == null) {
                    instance = new Router();
                }
            }
        }
        return instance;
    }

    public void init(Application application) {
//        loadInfo(application);
        mContext = application;
        try {
            loadRootInfo(application);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    public void navigation(String path,Activity activity) {
//        Class<? extends Activity> aClass = map.get(path);
//        if (aClass != null) {
//            Intent intent = new Intent(activity, aClass);
//            activity.startActivity(intent);
//        } else {
//            Log.e("zhouzhu","没有找到对应的页面");
//        }
//    }

//    private void loadInfo(Application application)  {
//        try {
//            Set<String> routeMap = ClassUtils.getFileNameByPackageName(application,"com.grechur.base");
//            for (String className : routeMap) {
//                Class<?> aClass = Class.forName(className);
//                if (IRouterInterface.class.isAssignableFrom(aClass)) {
//                    IRouterInterface load = (IRouterInterface) aClass.newInstance();
//                    load.loadRouter(map);
//                }
//            }
//        } catch (Exception e) {
//        }
//        for (Map.Entry<String, Class<? extends Activity>> stringClassEntry : map.entrySet()) {
//            Log.e("zhouzhu","key:"+stringClassEntry.getKey()+" value:"+stringClassEntry.getValue().getSimpleName());
//        }
//    }

    private void loadRootInfo(Application application) {
        try {
            Set<String> routeMap = ClassUtils.getFileNameByPackageName(application, "com.grechur.base");
            for (String route : routeMap) {
                if (route.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR +
                        SUFFIX_ROOT)) {
                    IRouteRoot routeRoot = (IRouteRoot) Class.forName(route).getConstructor().newInstance();
                    routeRoot.loadInto(Warehouse.groupIndex);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Postcard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return build(path, extractGroup(path));
        }
    }

    public Postcard build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return new Postcard(path, group);
        }
    }

    public Object navigation(Context context, Postcard postcard, int requestCode, NavigationCallback callback) {
        try {
            prepareCard(postcard);
        } catch (Exception e) {
            if (callback != null) {
                callback.onLost(postcard);
            }
            return null;
        }
        if (callback != null) {
            callback.onFound(postcard);
        }
        switch (postcard.getType()) {
            case ACTIVITY:
                Context currentContext = context == null ? mContext : context;
                Intent intent = new Intent(context, postcard.getDestination());
                intent.putExtras(postcard.getExtras());
                int flags = postcard.getFlag();
                if (-1 != flags) {
                    intent.setFlags(flags);
                } else if (!(currentContext instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (requestCode > 0) {
                            ActivityCompat.startActivityForResult((Activity) currentContext,intent,requestCode,postcard.getOptionsBundle());
                        } else {
                            ActivityCompat.startActivity(currentContext, intent, postcard
                                    .getOptionsBundle());
                        }
                        if ((0 != postcard.getEnterAnim() || 0 != postcard.getExitAnim()) &&
                                currentContext instanceof Activity) {
                            //老版本
                            ((Activity) currentContext).overridePendingTransition(postcard
                                            .getEnterAnim()
                                    , postcard.getExitAnim());
                        }
                        if (null != callback) {
                            callback.onArrival(postcard);
                        }
                    }
                });
                break;
            case ISERVICE:
                return postcard.getService();
            default:
                break;
        }
        return null;
    }

    private void prepareCard(Postcard card) {
        RouteMeta routeMeta = Warehouse.routes.get(card.getPath());
        if (routeMeta == null) {
            Class<? extends IRouteGroup> routeGroup = Warehouse.groupIndex.get(card.getGroup());
            if (routeGroup == null) {
                throw new RuntimeException("没找到对应路由: " + card.getGroup() + " " +
                        card.getPath());
            }
            IRouteGroup iRouteGroup;
            try {
                iRouteGroup = routeGroup.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("路由分组映射表记录失败.", e);
            }
            iRouteGroup.loadInto(Warehouse.routes);
            Warehouse.groupIndex.remove(card.getGroup());
            prepareCard(card);
        } else {
            card.setDestination(routeMeta.getDestination());
            card.setType(routeMeta.getType());
            switch (routeMeta.getType()) {
                case ISERVICE:
                    Class<?> destination = routeMeta.getDestination();
                    IService iService = Warehouse.services.get(destination);
                    if (null == iService) {
                        try {
                            iService = (IService) destination.getConstructor().newInstance();
                            Warehouse.services.put(destination, iService);
                        } catch (Exception e) {

                        }
                    }
                    break;
            }
        }
    }

    /**
     * 获得path中的组名
     *
     * @param path
     * @return
     */
    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new RuntimeException(path + " : 不能提取group.");
        }
        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
                throw new RuntimeException(path + " : 不能提取group.");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
