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
        String serverRegistration = configInstance.getStringProperty("eureka.vipAddress", "eureka-service:8085").get();
        InstanceInfo nextServerInfo = null;
        while (isaBoolean(nextServerInfo)) {
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

    private boolean isaBoolean(InstanceInfo nextServerInfo) {
        return nextServerInfo == null || !String.valueOf(nextServerInfo.getPort()).equals(eurekaClient.getEurekaClientConfig().getEurekaServerPort());
    }

    @PreDestroy
    public void closeConnectionServer(){
        if (eurekaClient != null) {
            System.out.println("Shutting down server. Demo over.");
            eurekaClient.shutdown();
        }
    }
}