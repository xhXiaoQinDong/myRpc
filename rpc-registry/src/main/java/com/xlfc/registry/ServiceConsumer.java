package com.xlfc.registry;

import com.xlfc.common.config.RpcRequest;
import com.xlfc.common.annotion.SPI;

import java.net.InetSocketAddress;


@SPI
public interface ServiceConsumer {

    InetSocketAddress getIpAndPort(RpcRequest rpcRequest);
}
