package com.electrician.spark_e;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.electrician.spark_e.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class SparkEApplication {

	public static void main(String[] args) {
		SpringApplication.run(SparkEApplication.class, args);
	}

}
