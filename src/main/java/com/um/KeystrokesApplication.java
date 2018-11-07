package com.um;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class KeystrokesApplication extends SpringBootServletInitializer {
	
	//to make sure the exported war file of this application project work the following code is necessary.Need to import SpringBootServletInitializer and extend the class to SpringBootServletInitializer
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(KeystrokesApplication.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(KeystrokesApplication.class, args);
	}
}
