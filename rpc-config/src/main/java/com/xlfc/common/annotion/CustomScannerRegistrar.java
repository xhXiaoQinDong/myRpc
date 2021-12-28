package com.xlfc.common.annotion;


import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;



public class CustomScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final String SPRING_BEAN_BASE_PACKAGE="com.xlfc";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME="basePackages";
    private static final Class<?> annotationClass = RpcComponentScan.class;
    private ResourceLoader resourceLoader;
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        CustomScanner rpcServiceScanner=new CustomScanner(beanDefinitionRegistry, Service.class);
        CustomScanner springBeanScanner=new CustomScanner(beanDefinitionRegistry, Component.class);

        if (resourceLoader!=null){
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
        }

        String[] packageToScan=getPackageToScan(annotationMetadata);

        //其实就是扫描包下面有这些注解的类，将其加入到容器中后才可以使用。

        springBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);

        rpcServiceScanner.scan(packageToScan);


    }

    /**
     * 获取到需要扫描的内容
     * */
    private String[] getPackageToScan(AnnotationMetadata annotationMetadata) {
        String[] packageToScan=new String[0];

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(annotationClass.getName()));//可见DubboComponentScanRegistrar的getPackagesToScan0方法

        if (attributes!=null){
            packageToScan=attributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        //说明是没有扫描的
        if (packageToScan.length==0){
            packageToScan=new String[]{((StandardAnnotationMetadata)annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }
        return packageToScan;
    }

}
