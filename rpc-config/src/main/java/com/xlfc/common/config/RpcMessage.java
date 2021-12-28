package com.xlfc.common.config;


public class RpcMessage {
    private byte messageType;
    private byte codec;
    private int requestId;
    private Object data;

    public RpcMessage(byte messageType, byte codec, int requestId, Object data) {
        this.messageType = messageType;
        this.codec = codec;
        this.requestId = requestId;
        this.data = data;
    }

    public RpcMessage() {

    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte getCodec() {
        return codec;
    }

    public void setCodec(byte codec) {
        this.codec = codec;
    }


    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
