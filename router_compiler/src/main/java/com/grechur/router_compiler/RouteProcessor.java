package com.grechur.router_compiler;

import com.google.auto.service.AutoService;
import com.grechur.route_annotation.RouteMeta;
import com.grechur.route_annotation.ZRoute;
import com.grechur.router_compiler.utils.Consts;
import com.grechur.router_compiler.utils.Log;
import com.grechur.router_compiler.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
@SupportedOptions(Consts.ARGUMENTS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({Consts.ANN_TYPE_ROUTE})
public class RouteProcessor extends AbstractProcessor {
    private Filer filerUtils;
    private Log log;
    private Elements elementUtils;
    private Types typeUtils;
    private Map<String, List<RouteMeta>> groupMap = new HashMap<>();
    /**
     * key:组名 value:类名
     */
    private Map<String, String> rootMap = new TreeMap<>();
    /**
     * 参数
     */
    private String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filerUtils = processingEnv.getFiler();
        log = Log.newLog(processingEnv.getMessager());
        log.i("init()");
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        Map<String, String> options = processingEnv.getOptions();
        if (!Utils.isEmpty(options)) {
            moduleName = options.get(Consts.ARGUMENTS_NAME);
            for (Map.Entry<String, String> entry : options.entrySet()) {
                log.i("key:" + entry.getKey()+ " value:"+entry.getValue());
            }
        }
        log.i("RouteProcessor Parmaters:" + moduleName);
        if (Utils.isEmpty(moduleName)) {
            throw new RuntimeException("Not set Processor Parmaters.");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        log.i("process");
//        MethodSpec main = MethodSpec.methodBuilder("main")
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .returns(void.class)
//                .addParameter(String[].class, "args")
//                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
//                .build();
//
//        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
//                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .addMethod(main)
//                .build();
//
//        JavaFile javaFile = JavaFile.builder("com.grechur.mytest", helloWorld)
//                .build();
//
//        try {
//            javaFile.writeTo(filerUtils);
//            log.i("write");
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.i(e.getMessage());
//        }

        if (set != null && !set.isEmpty()) {
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(ZRoute.class);
            if (routeElements != null && !routeElements.isEmpty()) {
                try {
                    log.i("route size" + routeElements.size());
                    parseRoutes(routeElements);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        return false;
    }

    private void parseRoutes(Set<? extends Element> routeElements) throws IOException, ClassNotFoundException {
        TypeElement activity = elementUtils.getTypeElement(Consts.ACTIVITY);
        TypeMirror typeMirror = activity.asType();

        TypeElement iService = elementUtils.getTypeElement(Consts.ISERVICE);
        TypeMirror type_IService = iService.asType();
        for (Element element : routeElements) {
            RouteMeta routeMeta;
            TypeMirror mirror = element.asType();
            ZRoute zRoute = element.getAnnotation(ZRoute.class);
            if (typeUtils.isSubtype(mirror, typeMirror) ) {
                routeMeta = new RouteMeta(RouteMeta.Type.ACTIVITY, zRoute, element);
            } else if (typeUtils.isSubtype(mirror, type_IService)) {
                routeMeta = new RouteMeta(RouteMeta.Type.ISERVICE, zRoute, element);
            } else {
                throw new RuntimeException("[Just Support Activity/IService Route] :" + element);
            }
            //分组信息记录  groupMap <Group分组,RouteMeta路由信息> 集合
            //检查是否配置 group 如果没有配置，则从path截取出组名
            categories(routeMeta);
        }

        TypeElement iRouteGroup = elementUtils.getTypeElement(Consts.IROUTE_GROUP);
        log.i("---------" + iRouteGroup.getSimpleName());
        TypeElement iRouteRoot = elementUtils.getTypeElement(Consts.IROUTE_ROOT);

        /**
         *  生成Group类 作用:记录 <地址,RouteMeta路由信息(Class文件等信息)>
         */
        generatedGroup(iRouteGroup);
        /**
         * 生成Root类 作用:记录 <分组，对应的Group类>
         */
        generatedRoot(iRouteRoot, iRouteGroup);

    }

    private void categories(RouteMeta route) {
        if (routeVerfiy(route)) {
            log.i("Group Info, Group Name = " + route.getGroup() + ", Path = " +
                    route.getPath());
            List<RouteMeta> routeMetas = groupMap.get(route.getGroup());
            if (Utils.isEmpty(routeMetas)) {
                List<RouteMeta> metas = new ArrayList<>();
                metas.add(route);
                groupMap.put(route.getGroup(), metas);
            } else {
                routeMetas.add(route);
            }
        } else {
            log.i("Group Info Error: " + route.getPath());
        }
    }

    /**
     * 验证group是否合法
     *
     * @param routeMeta
     * @return
     */
    private boolean routeVerfiy(RouteMeta routeMeta) {
        String path = routeMeta.getPath();
        String group = routeMeta.getGroup();
        if (Utils.isEmpty(path) || !path.startsWith("/")) return false;

        if (Utils.isEmpty(group)) {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (Utils.isEmpty(defaultGroup)) return false;
            routeMeta.setGroup(defaultGroup);
            return true;
        }
        return false;
    }

    private void generatedGroup(TypeElement routeGroup) throws IOException {
        //方法入参类型
        ParameterizedTypeName atlas = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class)
        );
        //方法入参创建
        ParameterSpec groupParameters = ParameterSpec.builder(atlas, "atlas").build();
        for (Map.Entry<String, List<RouteMeta>> entry : groupMap.entrySet()) {
            //遍历分组，每个分组创建一个类
            //创建类里面的方法 public void loadInfo(Map<String,RouteMeta> atlas)
            MethodSpec.Builder loadIntoBuilder = MethodSpec.methodBuilder(Consts.METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addParameter(groupParameters)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID);
            //分组名称
            String groupName = entry.getKey();
            //一个分组中有该类型注解条目
            List<RouteMeta> routeMetas = entry.getValue();
            for (RouteMeta meta : routeMetas) {
                //atlas.put(地址,RouteMeta.build(Class,path,group))
                loadIntoBuilder.addStatement(
                        "atlas.put($S,$T.build($T.$L,$T.class,$S,$S))",
                        meta.getPath(),
                        ClassName.get(RouteMeta.class),
                        ClassName.get(RouteMeta.Type.class),
                        meta.getType(),
                        ClassName.get((TypeElement) meta.getElement()),
                        meta.getPath().toLowerCase(),
                        meta.getGroup().toLowerCase()
                );
            }
            //创建java文件
            String groupClassName = Consts.NAME_OF_GROUP + groupName;
            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)
                    .addSuperinterface(ClassName.get(routeGroup))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(loadIntoBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE,typeSpec).build();
            javaFile.writeTo(filerUtils);
            log.i("Generated RouteGroup: " + Consts.PACKAGE_OF_GENERATE_FILE + "." +
                    groupClassName);
            rootMap.put(groupName,groupClassName);
        }
    }

    private void generatedRoot(TypeElement iRouteRoot, TypeElement iRouteGroup) throws IOException {
        //类型 Map<String,Class<? extends IRouteGroup>> routes>
        //Wildcard 通配符
        ParameterizedTypeName routes = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(iRouteGroup))
                )
        );

        //参数 Map<String,Class<? extends IRouteGroup>> routes> routes
        ParameterSpec rootParamSpec = ParameterSpec.builder(routes, "routes")
                .build();
        //函数 public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder
                        (Consts.METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(rootParamSpec);

        //函数体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            loadIntoMethodOfRootBuilder.addStatement("routes.put($S, $T.class)", entry
                    .getKey(), ClassName.get(Consts.PACKAGE_OF_GENERATE_FILE, entry.getValue
                    ()));
        }
        //生成 $Root$类
        String rootClassName = Consts.NAME_OF_ROOT + moduleName;
        JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(rootClassName)
                        .addSuperinterface(ClassName.get(iRouteRoot))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(loadIntoMethodOfRootBuilder.build())
                        .build()
        ).build().writeTo(filerUtils);

        log.i("Generated RouteRoot: " + Consts.PACKAGE_OF_GENERATE_FILE + "." + rootClassName);
    }
}
