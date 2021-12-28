package com.xlfc.consumer.controller;

import com.xlfc.api.HelloService;
import com.xlfc.common.annotion.Reference;
import org.springframework.stereotype.Component;


@Component
public class HelloController {


    @Reference(version = "version2",group = "group2")
    private HelloService helloService;

    public void test(){
        String result = this.helloService.sayHello("测试执行客户端");
        System.out.println(result);
    }
}
