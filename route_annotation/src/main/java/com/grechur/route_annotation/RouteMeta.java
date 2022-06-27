package com.grechur.route_annotation;

import javax.lang.model.element.Element;

public class RouteMeta {
    public enum Type {
        ACTIVITY,
        ISERVICE
    }

    private Type type;
    private Element element;

    private Class<?> destination;//注解使用的对象
    private String path;//路由地址
    private String group;//路由组

    public static RouteMeta build(Type type, Class<?> destination, String path, String group) {
        return new RouteMeta(type, null, destination, path, group);
    }


    public RouteMeta(Type type, Element element, Class<?> destination, String path, String group) {
        this.type = type;
        this.element = element;
        this.destination = destination;
        this.path = path;
        this.group = group;
    }

    public RouteMeta() {
    }

    /**
     * Type
     *
     * @param route   route
     * @param element element
     */
    public RouteMeta(Type type, ZRoute route, Element element) {
        this(type, element, null, route.path(), route.group());
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
