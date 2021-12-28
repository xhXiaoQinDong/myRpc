package com.xlfc.registry.zk;

import com.xlfc.common.config.RpcRequest;
import com.xlfc.common.exception.RpcException;
import com.xlfc.common.extension.ExtensionLoader;
import com.xlfc.cluster.LoadBalance;
import com.xlfc.registry.ServiceConsumer;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;


public class ZkServiceConsumerImpl implements ServiceConsumer {
    @Override
    public InetSocketAddress getIpAndPort(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();

        CuratorFramework zkClient = ZookeeperUtils.getZkClient();
        List<String> serviceUrlList = ZookeeperUtils.getChildrenNodes(zkClient,rpcServiceName);
        if (serviceUrlList==null || serviceUrlList.size()==0){
            throw new RpcException("未找到服务，该服务为："+rpcServiceName);
        }
        //做下负载均衡
        LoadBalance random = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("random");

//        List<String> list=new ArrayList<>();
//        for (int i = 0; i < 15; i++) {
//            list.add();
//        }
//
//        String targetServiceUrl = list.get(0);

        String targetServiceUrl = random.selectServiceAddress(serviceUrlList, rpcRequest);

        String[] socketAddressArray = targetServiceUrl.split(":");

        String host = socketAddressArray[0];

        int port = Integer.parseInt(socketAddressArray[1]);


        return new InetSocketAddress(host,port);
    }

}
