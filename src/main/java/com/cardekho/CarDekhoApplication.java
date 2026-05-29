package com.cardekho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CarDekhoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarDekhoApplication.class, args);
	}
}
