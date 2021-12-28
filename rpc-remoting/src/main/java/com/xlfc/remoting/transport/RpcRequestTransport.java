package com.xlfc.remoting.transport;

import com.xlfc.common.config.RpcRequest;

public interface RpcRequestTransport {

    Object sendRpcRequest(RpcRequest rpcRequest) throws Exception;
}
