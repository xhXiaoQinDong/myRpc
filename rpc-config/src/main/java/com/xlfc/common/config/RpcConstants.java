package com.xlfc.common.config;


public class RpcConstants {
    public static final String ZK_REGISTER_ROOT_PATH = "/xlfc-rpc";

    public static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";

    public static final byte[] MAGIC_NUMBER = {(byte) 'x', (byte) 'l', (byte) 'f', (byte) 'c'};

    public static final int HEAD_LENGTH = 16;

    public static final byte TOTAL_LENGTH = 16;

    public static final byte HEARTBEAT_REQUEST_TYPE = 3;

    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;


}
