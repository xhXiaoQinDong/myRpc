package com.xlfc.provider;


import com.xlfc.common.annotion.RpcComponentScan;
import com.xlfc.remoting.transport.netty.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


public class AnnotationProvider {
    public static void main(String[] args) throws IOException {
        new AnnotationConfigApplicationContext(ProviderComponentScan.class);
        //启动netty
        NettyRpcServer nettyRpcServer=new NettyRpcServer();
        nettyRpcServer.start();
    }

    @Configuration
    @RpcComponentScan(basePackages = {"com.xlfc.provider.impl"})
    static public class ProviderComponentScan{

    }
}
