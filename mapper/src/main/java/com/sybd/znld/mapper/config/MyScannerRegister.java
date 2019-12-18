package com.sybd.znld.mapper.config;

import com.sybd.znld.mapper.MyEnumTypeHandler;
import com.sybd.znld.model.IEnum;
import com.sybd.znld.model.MyEnum;
import com.sybd.znld.model.Status;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.ArrayList;
import java.util.List;

public class MyScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private ResourceLoader resourceLoader;
    public static List<Class<IEnum>> enums = new ArrayList<>();

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //获取所有注解的属性和值
        var annotationAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(MyScan.class.getName()));
        //获取到basePackage的值
        if(annotationAttrs == null) return;
        var basePackages = annotationAttrs.getStringArray("basePackage");
        //如果没有设置basePackage 扫描路径,就扫描对应包下面的值
        if(basePackages.length == 0){
            basePackages = new String[]{((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName()};
        }

        //自定义的包扫描器
        var scanner = new ClassPathBeanDefinitionScanner(registry,false);
        //scanner.addIncludeFilter(new AnnotationTypeFilter(MyEnum.class)); // 只有添加了这个注解才会被扫描到
        scanner.setResourceLoader(this.resourceLoader);
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(MyEnum.class)); // 只有添加了这个注解才会被扫描到
        provider.setResourceLoader(this.resourceLoader);
        for(var b : basePackages) {
            var beanDefinitions = provider.findCandidateComponents(b);
            for(var d : beanDefinitions) {
                Class<IEnum> clazz = null;
                try {
                    clazz = (Class<IEnum>) Class.forName(d.getBeanClassName());
                } catch (ClassNotFoundException ignored) {
                }
                if(clazz != null) {
                    enums.add(clazz);
                }
            }
        }
        //这里实现的是根据名称来注入
        //scanner.setBeanNameGenerator(new MyBeanNameGenerator());
        //扫描指定路径下的接口
        scanner.scan(basePackages);
    }
}
