package com.xlfc.remoting.transport.netty.client;

import com.xlfc.common.config.RpcConstants;
import com.xlfc.common.config.RpcMessage;
import com.xlfc.common.config.RpcResponse;
import com.xlfc.common.factory.SingletonFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

    public static final Map<String, CompletableFuture<RpcResponse<Object>>> COMPLETABLE_CLIENT=new ConcurrentHashMap<>();;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcMessage) {
                RpcMessage tmp = (RpcMessage) msg;
                byte messageType = tmp.getMessageType();
                if (messageType == RpcConstants.RESPONSE_TYPE) {
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) tmp.getData();

                    CompletableFuture<RpcResponse<Object>> future = COMPLETABLE_CLIENT.remove(rpcResponse.getRequestId());
                    if (null != future) {
                        future.complete(rpcResponse);
                    } else {
                        throw new IllegalStateException();
                    }

                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
