package com.xlfc.common.config;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.UUID;


public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -4355285085441097044L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private String version;
    private String group;

    public RpcRequest() {
    }

    public RpcRequest(String requestId, String interfaceName, String methodName, Object[] parameters, Class<?>[] paramTypes, String version, String group) {
        this.requestId = requestId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameters = parameters;
        this.paramTypes = paramTypes;
        this.version = version;
        this.group = group;
    }

    public RpcRequest(Method method, Object[] args, RpcService rpcServiceConfig) {
        this(UUID.randomUUID().toString(),method.getDeclaringClass().getName(),method.getName(),args,
                method.getParameterTypes(),rpcServiceConfig.getVersion(),rpcServiceConfig.getGroup());
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
