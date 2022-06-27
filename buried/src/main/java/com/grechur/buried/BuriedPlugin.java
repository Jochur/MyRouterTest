//package com.grechur.buried;
//
//import com.android.build.api.transform.DirectoryInput;
//import com.android.build.api.transform.Format;
//import com.android.build.api.transform.JarInput;
//import com.android.build.api.transform.QualifiedContent;
//import com.android.build.api.transform.Transform;
//import com.android.build.api.transform.TransformException;
//import com.android.build.api.transform.TransformInput;
//import com.android.build.api.transform.TransformInvocation;
//import com.android.build.api.transform.TransformOutputProvider;
//import com.android.build.gradle.AppExtension;
//import com.android.build.gradle.internal.pipeline.TransformManager;
//import com.android.utils.FileUtils;
//import com.google.common.collect.FluentIterable;
//
//import org.gradle.api.Plugin;
//import org.gradle.api.Project;
//import org.objectweb.asm.ClassReader;
//import org.objectweb.asm.ClassWriter;
//import org.objectweb.asm.Opcodes;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Collection;
//import java.util.Set;
//
//public class BuriedPlugin extends Transform implements Plugin<Project> {
//
//
//    @Override
//    public void apply(Project project) {
//        AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
//        appExtension.registerTransform(this);
//    }
//
//    @Override
//    public String getName() {
//        return "BuriedPlugin";
//    }
//
//    @Override
//    public Set<QualifiedContent.ContentType> getInputTypes() {
//        return TransformManager.CONTENT_CLASS;
//    }
//
//    @Override
//    public Set<? super QualifiedContent.Scope> getScopes() {
//        return TransformManager.SCOPE_FULL_PROJECT;
//    }
//
//    @Override
//    public boolean isIncremental() {
//        return false;
//    }
//
//    @Override
//    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
//        Collection<TransformInput> inputs = transformInvocation.getInputs();
//        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
//        for (TransformInput input : inputs) {
//            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
//                if (directoryInput.getFile().isDirectory()) {
//                    FluentIterable<File> allFiles = FileUtils.getAllFiles(directoryInput.getFile());
//                    for (File file : allFiles) {
//                        String name = file.getName();
//                        if (name.endsWith(".class") && name != "R.class" &&
//                                !name.startsWith("R$") && name != "BuildConfig.class") {
//                            String classPath = file.getAbsolutePath();
//                            ClassReader classReader = new ClassReader(file.toString().getBytes());
//                            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
//                            ActivityVisitor visitor = new ActivityVisitor(Opcodes.ASM7, classWriter);
//                            classReader.accept(visitor,ClassReader.SKIP_FRAMES);
//
//                            byte[] bytes = classWriter.toByteArray();
//                            FileOutputStream fos = new FileOutputStream(classPath);
//                            fos.write(bytes);
//                            fos.close();
//                        }
//                    }
//                }
//                File dest = outputProvider.getContentLocation(
//                        directoryInput.getName(),
//                        directoryInput.getContentTypes(),
//                        directoryInput.getScopes(),
//                        Format.DIRECTORY
//                );
//                FileUtils.copyFileToDirectory(directoryInput.getFile(),dest);
//            }
//            for (JarInput jarInput : input.getJarInputs()) {
//                File location = outputProvider.getContentLocation(
//                        jarInput.getName(),
//                        jarInput.getContentTypes(),
//                        jarInput.getScopes(),
//                        Format.JAR
//                );
//                FileUtils.copyFile(jarInput.getFile(),location);
//            }
//        }
//    }
//}
