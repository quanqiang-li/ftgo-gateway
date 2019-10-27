package com.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringCloudApplication
@EnableZuulProxy
public class GatewayApplication {

	
	public static void main(String[] args) {
		new SpringApplication(GatewayApplication.class).run(args);
	}
}
