package com.whatever.util;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;
import java.util.regex.Pattern;

public class MyPackageScanner {
    /*private void scanPackage(String packageName, File currentfile) {
        File[] files = currentfile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (currentfile.isDirectory()) {
                    return true;
                }
                return pathname.getName().endsWith(".class");
            }
        });
        for (File file : files) {
            if (file.isDirectory()) {
                scanPackage(packageName + "." + file.getName(), file);
            } else {
                String fileName = file.getName().replace(".class", "");
                String className = packageName + "." + fileName;
                try {
                    Class<?> klass = Class.forName(className);
                    if (klass.isAnnotation()
                            ||klass.isInterface()
                            ||klass.isEnum()
                            ||klass.isPrimitive()) {
                        continue;
                    }
                    dealClass(klass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public static void main(String[] args){
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));

        Set<BeanDefinition> classes = provider.findCandidateComponents("com.whatever");

        for (var bean: classes) {
            try {
                var clazz = Class.forName(bean.getBeanClassName());
                System.out.println(clazz.getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }*/
}
