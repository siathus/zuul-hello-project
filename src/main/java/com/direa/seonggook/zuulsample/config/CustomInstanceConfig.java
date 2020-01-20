package com.direa.seonggook.zuulsample.config;

import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.PropertiesInstanceConfig;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.internal.util.Archaius1Utils;

public class CustomInstanceConfig extends PropertiesInstanceConfig implements EurekaInstanceConfig {
    protected final String namespace;
    protected final DynamicPropertyFactory configInstance;

    public CustomInstanceConfig(String namespace) {
        super(namespace);
        this.namespace = namespace.endsWith(".") ? namespace : namespace + ".";
        // Default "eureka-client"에서 "config"로 변경
        this.configInstance = Archaius1Utils.initConfig("config");
    }
}
