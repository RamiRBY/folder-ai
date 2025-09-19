package com.folderai.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FolderAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FolderAiApplication.class, args);
	}

}
