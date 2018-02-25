package ru.netflix.servicenoboot;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ServiceInstanceRestController {

    @RequestMapping("/service-instances/{applicationName}")
    public String serviceInstancesByApplicationName(@PathVariable String applicationName) {
        return "response: " + applicationName;
    }
}
