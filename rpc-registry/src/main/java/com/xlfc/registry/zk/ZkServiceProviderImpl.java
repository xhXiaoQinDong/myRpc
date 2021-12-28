package com.xlfc.registry.zk;

import com.xlfc.common.config.RpcConstants;
import com.xlfc.common.config.RpcService;
import com.xlfc.common.exception.RpcException;
import com.xlfc.registry.ServiceProvider;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class ZkServiceProviderImpl implements ServiceProvider {
    private static final String LOCAL_HOST="0.0.0.0";
    private static final String TEST_IP="20.20.20.20";

    private final Map<String,Object> serviceMap=new ConcurrentHashMap<>();
    private final Set<String> registeredService=ConcurrentHashMap.newKeySet();
    private final CuratorFramework zkClient= ZookeeperUtils.getZkClient();

    @Override
    public void register(RpcService rpcService) {
        this.addService(rpcService);

        InetSocketAddress address = new InetSocketAddress(rpcService.getHost(), rpcService.getPort());

        String servicePath= RpcConstants.ZK_REGISTER_ROOT_PATH+"/"+rpcService.getServiceName()+rpcService.getGroup()+rpcService.getVersion()+address;

        ZookeeperUtils.createPersistentNode(zkClient,servicePath);
    }

    @Override
    public void addService(RpcService rpcService) {
        String serviceName = rpcService.getRpcServiceName();

        if (registeredService.contains(serviceName)){
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName,rpcService.getService());
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null==service){
            throw new RpcException("未找到服务，该服务为："+rpcServiceName);
        }

        return service;
    }

}
