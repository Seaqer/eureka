package ru.netflix.clientnoboot;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.loadbalancer.*;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.springframework.http.HttpMethod.GET;

public class ExampleEurekaClient {

    public static void main(String[] args) throws IOException {
        //по умолчанию ищет файл eureka-client.properties
        ConfigurationManager.loadPropertiesFromResources("application.properties");

        ApplicationInfoManager applicationInfoManager = initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
        EurekaClient client = initializeEurekaClient(applicationInfoManager, new DefaultEurekaClientConfig());

        ExampleEurekaClient sampleClient = new ExampleEurekaClient();
        sampleClient.sendRequest(client);
        client.shutdown();
    }

    private static synchronized ApplicationInfoManager initializeApplicationInfoManager(EurekaInstanceConfig instanceConfig) {
        InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
        return new ApplicationInfoManager(instanceConfig, instanceInfo);
    }

    private static synchronized EurekaClient initializeEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig clientConfig) {
        return new DiscoveryClient(applicationInfoManager, clientConfig);
    }

    private void sendRequest(EurekaClient eurekaClient) {
        String serviceName = "eureka-service";
        IRule rule = new AvailabilityFilteringRule();
        findServer(eurekaClient, serviceName, rule);


        InstanceInfo nextServerInfo = createInstanceInfo(eurekaClient, serviceName);

        try {
            RestTemplate restTemplate = new RestTemplate();
            String body = restTemplate.exchange(nextServerInfo.getHomePageUrl() + "service-instances/{serviceName}", GET, null, new ParameterizedTypeReference<String>() {
            }, serviceName).getBody();

            System.out.println("response: " + body);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void findServer(EurekaClient eurekaClient, String serviceName, IRule rule) {
        ServerList<DiscoveryEnabledServer> list = new DiscoveryEnabledNIWSServerList(serviceName, () -> eurekaClient);
        ServerListFilter<DiscoveryEnabledServer> filter = new ZoneAffinityServerListFilter<>();
        ZoneAwareLoadBalancer<DiscoveryEnabledServer> lb = LoadBalancerBuilder.<DiscoveryEnabledServer>newBuilder()
                .withDynamicServerList(list)
                .withRule(rule)
                .withServerListFilter(filter)
                .buildDynamicServerListLoadBalancer();
        DiscoveryEnabledServer server = (DiscoveryEnabledServer) lb.chooseServer();
    }

    private InstanceInfo createInstanceInfo(EurekaClient eurekaClient, String addressService) {
        InstanceInfo nextServerInfo = null;
        try {
            nextServerInfo = eurekaClient.getNextServerFromEureka(addressService, false);
        } catch (Exception e) {
            System.err.println("Cannot get an instance of example service to talk to from eureka");
            System.exit(-1);
        }
        return nextServerInfo;
    }
}
