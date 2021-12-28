package com.xlfc.cluster;

import com.xlfc.common.annotion.SPI;
import com.xlfc.common.config.RpcRequest;

import java.util.List;

@SPI
public interface LoadBalance {

    String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest);
}

