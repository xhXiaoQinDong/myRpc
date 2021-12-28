package com.xlfc.remoting.transport;

import com.xlfc.common.annotion.Reference;
import com.xlfc.common.annotion.Service;
import com.xlfc.common.config.RpcService;
import com.xlfc.common.factory.SingletonFactory;
import com.xlfc.registry.ServiceProvider;
import com.xlfc.registry.zk.ZkServiceProviderImpl;
import com.xlfc.remoting.transport.netty.client.NettyRpcClient;
import com.xlfc.remoting.transport.netty.client.RpcClientProxy;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;

@Component
public class RpcAnnotationProcessor implements ApplicationListener<ContextRefreshedEvent> {
    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient=new NettyRpcClient();
    public static final int PORT=3170;

    public RpcAnnotationProcessor() {//创建服务提供者实例、以及netty实例
        this.serviceProvider= SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (Objects.isNull(event.getApplicationContext().getParent())){
            ApplicationContext context = event.getApplicationContext();

            //处理提供端/生产者
            handlerProvider(context);

            //处理消费端
            handlerConsumer(context);
        }
    }

    @SneakyThrows
    private void handlerProvider(ApplicationContext context) throws UnknownHostException {
        Map<String, Object> beans = context.getBeansWithAnnotation(Service.class);
        String host = InetAddress.getLocalHost().getHostAddress();
        if (!beans.isEmpty()){
            for (Object bean:beans.values()){
                Service service = bean.getClass().getAnnotation(Service.class);
                RpcService rpcServiceConfig=new RpcService(host,PORT,bean,service.version(),service.group());
                serviceProvider.register(rpcServiceConfig);
            }
        }
    }


    private void handlerConsumer(ApplicationContext context) {
        Map<String, Object> beans = context.getBeansWithAnnotation(Component.class);
        if (!beans.isEmpty()){
            for (Object bean:beans.values()){
                Class<?> targetClass = bean.getClass();
                Field[] declaredFields =targetClass.getDeclaredFields();

                for (Field declaredField:declaredFields){
                    Reference rpcReference = declaredField.getAnnotation(Reference.class);
                    if (rpcReference!=null){
                        RpcService rpcServiceConfig=new RpcService(rpcReference.version(),rpcReference.group());
                        RpcClientProxy rpcClientProxy=new RpcClientProxy(rpcClient,rpcServiceConfig);
                        Object clientProxy=rpcClientProxy.getProxy(declaredField.getType());
                        declaredField.setAccessible(true);
                        try {
                            declaredField.set(bean,clientProxy);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }



}
