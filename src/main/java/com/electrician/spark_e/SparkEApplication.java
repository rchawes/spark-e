package com.electrician.spark_e;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SparkEApplication {

	public static void main(String[] args) {
		SpringApplication.run(SparkEApplication.class, args);
	}

}
