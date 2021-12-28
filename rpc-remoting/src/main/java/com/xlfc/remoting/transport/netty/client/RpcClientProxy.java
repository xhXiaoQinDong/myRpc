package com.xlfc.remoting.transport.netty.client;

import com.xlfc.common.config.RpcRequest;
import com.xlfc.common.config.RpcResponse;
import com.xlfc.common.config.RpcService;
import com.xlfc.remoting.transport.RpcRequestTransport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;


public class RpcClientProxy {

    private final RpcRequestTransport rpcRequestTransport;
    private final RpcService rpcServiceConfig;

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, RpcService rpcServiceConfig) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }


    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},new ConsumerInvocationHandler());
    }

    private class ConsumerInvocationHandler implements InvocationHandler{
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            RpcRequest rpcRequest=new RpcRequest(method,args,rpcServiceConfig);

            RpcResponse<Object> rpcResponse=null;

            CompletableFuture<RpcResponse<Object>> completableFuture= (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport.sendRpcRequest(rpcRequest);

            rpcResponse=completableFuture.get();

            return rpcResponse.getData();
        }


    }

}
