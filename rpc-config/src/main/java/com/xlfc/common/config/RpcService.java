package com.xlfc.common.config;


public class RpcService {
    private String host;

    private int port;

    private Object service;

    private String version = "";

    private String group = "";

    public RpcService(String host, int port, Object service, String version, String group) {
        this.host = host;
        this.port = port;
        this.service = service;
        this.version = version;
        this.group = group;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public RpcService(String version, String group, Object service) {
        this.version = version;
        this.group = group;
        this.service = service;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

    public RpcService(String version, String group) {
        this.version = version;
        this.group = group;
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

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }


}
