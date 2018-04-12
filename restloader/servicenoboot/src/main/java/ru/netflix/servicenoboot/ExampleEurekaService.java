package ru.netflix.servicenoboot;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.io.IOException;

@Component
@Singleton
public class ExampleEurekaService {
    private final ApplicationInfoManager applicationInfoManager;
    private final EurekaClient eurekaClient;

    @Autowired
    public ExampleEurekaService(ApplicationInfoManager applicationInfoManager, EurekaClient eurekaClient) {
        this.applicationInfoManager = applicationInfoManager;
        this.eurekaClient = eurekaClient;
    }

    @PostConstruct
    public void registrationOnServer() throws IOException {
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        waitForRegistrationWithEureka();
    }

    private void waitForRegistrationWithEureka() {
        DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory.getInstance();
        // my vip address to listen on
        String serverRegistration = configInstance.getStringProperty("eureka.vipAddress", null).get();
        InstanceInfo nextServerInfo = null;
        while (isMe(nextServerInfo)) {
            try {
                nextServerInfo = eurekaClient.getNextServerFromEureka(serverRegistration, false);
            } catch (Throwable e) {
                System.out.println("Waiting ... verifying service registration with eureka ...");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private boolean isMe(InstanceInfo nextServerInfo) {
        return nextServerInfo == null ||
                !String.valueOf(nextServerInfo.getInstanceId()).equals(applicationInfoManager.getEurekaInstanceConfig().getInstanceId());
    }

    @PreDestroy
    public void closeConnectionServer(){
        if (eurekaClient != null) {
            System.out.println("Shutting down server. Demo over.");
            eurekaClient.shutdown();
        }
    }
}