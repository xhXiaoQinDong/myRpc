package com.xlfc.registry;

import com.xlfc.common.config.RpcService;
import com.xlfc.common.annotion.SPI;

@SPI
public interface ServiceProvider {

    void register(RpcService rpcService);


    void addService(RpcService rpcService);


    Object getService(String rpcServiceName);
}

