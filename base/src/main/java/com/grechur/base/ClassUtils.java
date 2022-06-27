package com.grechur.base;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import dalvik.system.DexFile;

public class ClassUtils {
    public static Set<String> getFileNameByPackageName(Application context,String packageName) throws PackageManager.NameNotFoundException, InterruptedException {
        Set<String> classNames = new HashSet<>();
        List<String> sourcePath = getSourcePath(context);
        CountDownLatch parserCtl = new CountDownLatch(sourcePath.size());
        ThreadPoolExecutor threadPoolExecutor = DefaultPoolExecutor.newDefaultPoolExecutor(sourcePath.size());
        for (String source : sourcePath) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    DexFile dexfile = null;
                    try {
                        dexfile = new DexFile(source);
                        Enumeration<String> entries = dexfile.entries();
                        while (entries.hasMoreElements()) {
                            String element = entries.nextElement();
                            if (element.startsWith(packageName)) {
                                classNames.add(element);
                            }
                        }
                    } catch (Exception e) {

                    } finally {
                        if (null != dexfile) {
                            try {
                                dexfile.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //释放1个
                        parserCtl.countDown();
                    }
                }
            });
        }
        parserCtl.await();
        return classNames;
    }

    private static List<String> getSourcePath(Application context) throws PackageManager.NameNotFoundException {
        ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        List<String> sourcePath = new ArrayList<>();
        sourcePath.add(applicationInfo.sourceDir);
        if (null != applicationInfo.splitSourceDirs) {
            sourcePath.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
        }
        return sourcePath;
    }
}
