package com.xlfc.serialization;

import com.xlfc.common.config.RpcConstants;
import com.xlfc.common.config.RpcMessage;
import com.xlfc.common.config.RpcRequest;
import com.xlfc.common.config.RpcResponse;
import com.xlfc.common.extension.ExtensionLoader;
import com.xlfc.serialization.serializtion.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    Logger log = LoggerFactory.getLogger(RpcMessageDecoder.class);
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

    public RpcMessageDecoder() {

        this(MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }


    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            int i = frame.readableBytes();
            if (i >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }

        }
        return decoded;
    }


    private Object decodeFrame(ByteBuf in) {
        //解决粘包
        checkMagicNumber(in);
        in.readByte();
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        int requestId = in.readInt();

        RpcMessage rpcMessage=new RpcMessage();
        rpcMessage.setCodec(codecType);
        rpcMessage.setRequestId(requestId);
        rpcMessage.setMessageType(messageType);

        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);

            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension("kryo");

            //反序列化
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmpValue);
            } else {
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;

    }

    private void checkMagicNumber(ByteBuf in) {
        byte[] tmp = new byte[RpcConstants.MAGIC_NUMBER.length];
        in.readBytes(tmp);
        for (int i = 0; i < RpcConstants.MAGIC_NUMBER.length; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }
}
