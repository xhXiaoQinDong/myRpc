package com.xlfc.remoting.transport.netty.client;

import com.xlfc.common.config.RpcConstants;
import com.xlfc.common.config.RpcMessage;
import com.xlfc.common.config.RpcRequest;
import com.xlfc.common.config.RpcResponse;
import com.xlfc.common.enums.SerializationTypeEnum;
import com.xlfc.common.extension.ExtensionLoader;
import com.xlfc.registry.ServiceConsumer;
import com.xlfc.remoting.transport.RpcRequestTransport;
import com.xlfc.serialization.RpcMessageDecoder;
import com.xlfc.serialization.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NettyRpcClient implements RpcRequestTransport {
    private final ServiceConsumer serviceConsumer;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final Map<String,Channel> channelMap;

    public NettyRpcClient(){
        channelMap=new ConcurrentHashMap<>();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyClientHandler());
                    }
                });
        this.serviceConsumer = ExtensionLoader.getExtensionLoader(ServiceConsumer.class).getExtension("zk");
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) throws Exception {
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();

        InetSocketAddress inetSocketAddress = serviceConsumer.getIpAndPort(rpcRequest);//获取到ip与地址。

        Channel channel=getChannel(inetSocketAddress);

        if (channel.isActive()){
            NettyClientHandler.COMPLETABLE_CLIENT.put(rpcRequest.getRequestId(),resultFuture);
            RpcMessage rpcMessage=this.createRpcMessage(rpcRequest);

            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future->{
                if (!future.isSuccess()){
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                }
            });


        }else{
            throw new IllegalStateException();
        }
        return resultFuture;

    }



    public Channel getChannel(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        Channel channel=this.getChannelFromMap(inetSocketAddress.toString());
        if (channel==null){
            channel=doConnect(inetSocketAddress);
            this.channelMap.put(inetSocketAddress.toString(),channel);
        }
        return channel;
    }

    private Channel getChannelFromMap(String key) {
        if (channelMap.containsKey(key)){
            Channel channel = channelMap.get(key);
            if (channel!=null && channel.isActive()){
                return channel;
            }else {
                channelMap.remove(key);
            }
        }
        return null;
    }


    private Channel doConnect(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture=new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future->{
            if (future.isSuccess()){
                completableFuture.complete(future.channel());
            }else{
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    private RpcMessage createRpcMessage(RpcRequest rpcRequest) {
        RpcMessage rpcMessage=new RpcMessage();
        rpcMessage.setData(rpcRequest);
        rpcMessage.setCodec(SerializationTypeEnum.KRYO.getCode());
        rpcMessage.setMessageType(RpcConstants.REQUEST_TYPE);
        return rpcMessage;
    }
}
