package com.xlfc.provider.impl;

import com.xlfc.api.HelloService;
import com.xlfc.common.annotion.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service(group = "group1",version = "version1")
public class HelloServiceImpl implements HelloService {
    private static Logger log = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String sayHello(String userName) {
        log.info("执行了服务端的sayHello方法，userName参数为"+userName+"服务端已成功执行服务");
        return "结果："+userName+"已成功执行";
    }
}
