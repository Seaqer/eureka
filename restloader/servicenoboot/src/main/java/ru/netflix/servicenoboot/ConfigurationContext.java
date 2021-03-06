package ru.netflix.servicenoboot;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class ConfigurationContext {

    @Bean
    public EurekaInstanceConfig instanceConfig() throws IOException {
        ConfigurationManager.loadPropertiesFromResources("application.properties");
        overridePropertyFromApplication();
        MyDataCenterInstanceConfig myDataCenterInstanceConfig = new MyDataCenterInstanceConfig();
        return myDataCenterInstanceConfig;
    }

    private void overridePropertyFromApplication() {
        Properties props = new Properties();
        props.setProperty("eureka.instanceId", "092183921803");
        ConfigurationManager.loadProperties(props);
    }

    @Bean
    public InstanceInfo instanceInfo(EurekaInstanceConfig instanceConfig) {
        return new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
    }

    @Bean
    public ApplicationInfoManager applicationInfoManager(InstanceInfo instanceInfo, EurekaInstanceConfig instanceConfig) {
        ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.STARTING);
        return applicationInfoManager;
    }

    @Bean
    public EurekaClient eurekaClient(ApplicationInfoManager applicationInfoManager) {
        return new DiscoveryClient(applicationInfoManager, new DefaultEurekaClientConfig());
    }
}
