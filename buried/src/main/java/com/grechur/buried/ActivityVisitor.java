package com.grechur.buried;

import com.grechur.buried_annotation.BuriedMethod;
import com.grechur.buried_annotation.BuriedMethodParam;
import com.grechur.buried_annotation.BuriedPageParams;

import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class ActivityVisitor extends ClassVisitor {
    private String mClassName;
    private String mParentName;
    private String[] interFaces;
    public ActivityVisitor(int api) {
        super(Opcodes.ASM7);
    }

    public ActivityVisitor(int api, ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, final String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
        methodVisitor = new AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, descriptor) {
            private boolean isClick = false;
            private boolean isPageParam = false;
            private boolean isMethodParam = false;
            @Override
            public void visitCode() {
                super.visitCode();
            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                System.out.println("AnnotationVisitor:"+desc+" visible:"+visible);
                String methodName = Type.getDescriptor(BuriedMethod.class);
                if (methodName.equals(desc)) {
                    isClick = true;
                    try {
                        //拿不到
//                        Class<?> aClass = Class.forName(mClassName);
//                        Method method = aClass.getMethod(name);
//                        BuriedMethod annotation = method.getAnnotation(BuriedMethod.class);
//                        buriedMethodName = annotation.click();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return super.visitAnnotation(desc, visible);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                System.out.println("visitFieldInsn:"+owner+" name:"+name+" descriptor:"+descriptor+" signature:"+signature);
                super.visitFieldInsn(opcode, owner, name, descriptor);
            }

            @Override
            protected void onMethodEnter() {
                super.onMethodEnter();
            }

            private boolean isInject() {
                //如果父类名是AppCompatActivity则拦截这个方法,实际应用中可以换成自己的父类例如BaseActivity
                if (mParentName.contains("AppCompatActivity")) {
                    System.out.println("isInject:"+mClassName);
                    return true;
                }
                return false;
            }

            @Override
            protected void onMethodExit(int opcode) {
                if (isInject()) {
                    if ("onCreate".endsWith(name)) {
                        mv.visitVarInsn(ALOAD,0);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, mClassName, "pageParams", "Ljava/util/Map;");
                        mv.visitMethodInsn(
                                INVOKESTATIC,
                                "com/grechur/base/TraceUtils",
                                "onActivityCreate",
                                "(Landroid/app/Activity;Ljava/util/Map;)V",
                                false);
                    } else if ("onDestroy".equals(name)) {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, mClassName, "pageParams", "Ljava/util/Map;");
                        mv.visitMethodInsn(INVOKESTATIC,
                                "com/grechur/base/TraceUtils"
                                , "onActivityDestroy",
                                "(Landroid/app/Activity;Ljava/util/Map;)V",
                                false);
                    }
                }

                if (isClick) {
//                    mv.visitVarInsn(ALOAD, 0);
//                    mv.visitFieldInsn(GETFIELD,mClassName,"clickName","Ljava/lang/String;");
//                    mv.visitVarInsn(ALOAD, 0);
//                    mv.visitFieldInsn(GETFIELD, mClassName, "methodParams", "Ljava/util/Map;");
//                    mv.visitMethodInsn(
//                            INVOKEINTERFACE,//注意这个参数不同
//                            "com/grechur/base/TraceUtils",
//                            "onClickMethod",
//                            "(Ljava/lang/String;Ljava/util/Map;)V",
//                            false
//                    );

                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, mClassName, "clickName", "Ljava/lang/String;");
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, mClassName, "methodParams", "Ljava/util/Map;");
                    mv.visitMethodInsn(
                            INVOKESTATIC,
                            "com/grechur/base/TraceUtils",
                            "onClickMethod",
                            "(Ljava/lang/String;Ljava/util/Map;)V",
                            false
                    );

//                    mv.visitVarInsn(ALOAD, 0);
//                    mv.visitFieldInsn(GETFIELD, mClassName, "clickName", "Ljava/lang/String;");
//                    mv.visitMethodInsn(INVOKESTATIC,
//                            "com/grechur/base/TraceUtils",
//                            "onClickMethod", "(Ljava/lang/String;)V",
//                            false);

                }
            }
        };

        return methodVisitor;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.mClassName = name;
        this.mParentName = superName;
        this.interFaces = interfaces;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        System.out.println("visitAnnotation:"+descriptor+" visible:"+visible);
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        System.out.println("visitField:"+access+" name:"+name+" descriptor:"+descriptor+" signature:"+signature+" value:"+value);
        return super.visitField(access, name, descriptor, signature, value);
    }
}