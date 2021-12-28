package com.xlfc.remoting.transport.netty.server;

import com.xlfc.common.config.RpcConstants;
import com.xlfc.common.config.RpcMessage;
import com.xlfc.common.config.RpcRequest;
import com.xlfc.common.config.RpcResponse;
import com.xlfc.common.enums.RpcResponseCodeEnum;
import com.xlfc.common.exception.RpcException;
import com.xlfc.common.factory.SingletonFactory;
import com.xlfc.registry.ServiceProvider;
import com.xlfc.registry.zk.ZkServiceProviderImpl;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    private final ServiceProvider serviceProvider= SingletonFactory.getInstance(ZkServiceProviderImpl.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcMessage) {

                RpcResponse<Object> rpcResponse;
                byte messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage=new RpcMessage();

                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                } else {

                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();

                    log.info("服务端接收一条新消息：请求id为"+rpcRequest.getRequestId()+"，接口为"+rpcRequest.getInterfaceName()+"，方法为"+rpcRequest.getMethodName());

                    Object result = this.handlerRequest(rpcRequest);
                    rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);

                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        rpcResponse= RpcResponse.success(result, rpcRequest.getRequestId());
                    } else {
                        rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                    }
                    rpcMessage.setData(rpcResponse);
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    public Object handlerRequest(RpcRequest invocation) {
        Object service = serviceProvider.getService(invocation.getRpcServiceName());
        Object result;
        try {
            Method method = service.getClass().getMethod(invocation.getMethodName(), invocation.getParamTypes());
            result = method.invoke(service, invocation.getParameters());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }

}
