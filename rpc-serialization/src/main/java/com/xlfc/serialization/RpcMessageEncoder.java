package com.xlfc.serialization;

import com.xlfc.common.extension.ExtensionLoader;
import com.xlfc.common.config.RpcConstants;
import com.xlfc.common.config.RpcMessage;
import com.xlfc.serialization.serializtion.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.atomic.AtomicInteger;

public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static Logger log = LoggerFactory.getLogger(RpcMessageEncoder.class);

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) throws Exception {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writerIndex(out.writerIndex() + 5);

            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            out.writeByte(rpcMessage.getCodec());
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());
            byte[] bodyBytes = null;

            int fullLength = RpcConstants.HEAD_LENGTH;
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                //序列化
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                        .getExtension("kryo");
                bodyBytes = serializer.serialize(rpcMessage.getData());
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex()+1;
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length+1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("编码失败");
        }
    }
}
