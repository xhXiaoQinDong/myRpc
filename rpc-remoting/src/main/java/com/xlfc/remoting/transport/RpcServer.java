package com.xlfc.remoting.transport;

import java.net.UnknownHostException;

/**
 * @Author: xiaoqindong
 * @CreateDate: 2021-12-27
 * @Description:
 */
public interface RpcServer {

    void start() throws UnknownHostException;

    void stop();
}
