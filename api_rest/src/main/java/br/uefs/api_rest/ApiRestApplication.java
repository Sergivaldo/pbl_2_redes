package br.uefs.api_rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiRestApplication {
	public static Car car = null;


	public static void main(String[] args) {

		// Gerar esses dados aleatoriamente
		int[] location = {100,22};
		String idCar = "0001";
		int battery = 100;
		float distanceForKMRateByPercentage = 4;
		float timePerKmTraveled = 2;

		car = new Car(timePerKmTraveled, location, idCar, battery, distanceForKMRateByPercentage);
		new CheckBattery().start();

		SpringApplication.run(ApiRestApplication.class, args);
	}
}
