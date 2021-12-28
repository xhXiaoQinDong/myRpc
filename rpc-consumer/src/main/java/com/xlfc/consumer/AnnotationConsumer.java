package com.xlfc.consumer;

import com.xlfc.common.annotion.RpcComponentScan;
import com.xlfc.consumer.controller.HelloController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


public class AnnotationConsumer {
    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(ProviderComponentScan.class);
        annotationConfigApplicationContext.start();

        final HelloController helloController = (HelloController) annotationConfigApplicationContext.getBean("helloController");
        helloController.test();
        System.in.read();
    }

    @Configuration
    @RpcComponentScan(basePackages = {"com.xlfc.consumer"})
    static public class ProviderComponentScan{

    }
}