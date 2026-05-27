package org.droneshow.package_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.droneshow.package_service")
public class PackageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PackageServiceApplication.class, args);
	}

}
