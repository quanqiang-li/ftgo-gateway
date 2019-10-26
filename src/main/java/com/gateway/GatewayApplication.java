package com.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {

	
	public static void main(String[] args) {
		new SpringApplication(GatewayApplication.class).run(args);
	}
}
