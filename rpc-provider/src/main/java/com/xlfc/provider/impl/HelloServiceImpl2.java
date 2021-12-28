package com.xlfc.provider.impl;

import com.xlfc.api.HelloService;
import com.xlfc.common.annotion.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(group = "group2",version = "version2")
public class HelloServiceImpl2 implements HelloService {
    private static Logger log = LoggerFactory.getLogger(HelloServiceImpl2.class);

    @Override
    public String sayHello(String userName) {
        log.info("执行了第二个实现方法");
        return userName+"：已完成";
    }
}
