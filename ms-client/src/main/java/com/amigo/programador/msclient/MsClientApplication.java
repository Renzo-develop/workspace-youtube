package com.amigo.programador.msclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsClientApplication {

	public static void main(String[] args) {
		System.out.println(">>>>> Estamos iniciando ms-client 1 <<<<<<<<<<");
		SpringApplication.run(MsClientApplication.class, args);
	}

}
