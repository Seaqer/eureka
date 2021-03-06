package ru.netflix.restclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

@RestController
class ServiceInstanceRestController {

    public static final String URL = "http://eureka-service/service-instances/{applicationName}";
    public static final String URL_ZUUL_SERVICE = "http://localhost:7777/service/by-service/service-instances/{applicationName}";
    public static final String URL_ZUUL_HOST = "http://localhost:7777/service/by-address/service-instances/{applicationName}";
    public static final RestTemplate NO_BALANCED_TEMPLATE = new RestTemplate();

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/service/{applicationName}/enable", method = RequestMethod.GET)
    public String getStudents(@PathVariable String applicationName) {
        System.out.println("Getting Application details for " + applicationName);
        String response = restTemplate.exchange(URL, GET, null, new ParameterizedTypeReference<String>() {
        }, applicationName).getBody();

        System.out.println("Response Received as " + response);
        return "Application Name -  " + applicationName + " Details: \n" + response;
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @RequestMapping(value = "/service/{applicationName}/zuul", method = RequestMethod.GET)
    public String getStudentsFromZuulService(@PathVariable String applicationName) {
        System.out.println("Getting Application details for " + applicationName);
        String response = NO_BALANCED_TEMPLATE.exchange(URL_ZUUL_SERVICE, GET, null, new ParameterizedTypeReference<String>() {
        }, applicationName).getBody();

        System.out.println("Response Received as " + response);
        return "Application Name -  " + applicationName + " Details: \n" + response;
    }

    @RequestMapping(value = "/address/{applicationName}/zuul", method = RequestMethod.GET)
    public String getStudentsFromZuulAddress(@PathVariable String applicationName) {
        System.out.println("Getting Application details for " + applicationName);
        String response = NO_BALANCED_TEMPLATE.exchange(URL_ZUUL_HOST, GET, null, new ParameterizedTypeReference<String>() {
        }, applicationName).getBody();

        System.out.println("Response Received as " + response);
        return "Application Name -  " + applicationName + " Details: \n" + response;
    }
}
