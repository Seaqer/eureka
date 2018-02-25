package ru.netflix.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class ServiceInstanceRestController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/service-instances/{applicationName}")
    public String serviceInstancesByApplicationName(@PathVariable String applicationName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(applicationName);
        StringBuilder stringBuilder = new StringBuilder();
        for (ServiceInstance instance : instances) {
            stringBuilder.append(instance.getUri())
                    .append(" host: ").append(instance.getHost())
                    .append(" port: ").append(instance.getPort())
                    .append(" service id:  ").append(instance.getServiceId())
                    .append(" \n");
        }

        return stringBuilder.toString();
    }
}
