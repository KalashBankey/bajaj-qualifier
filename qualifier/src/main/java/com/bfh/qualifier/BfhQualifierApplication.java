package com.bfh.qualifier;

import com.bfh.qualifier.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BfhQualifierApplication implements CommandLineRunner {

	@Autowired
	private WebhookService webhookService;

	public static void main(String[] args) {
		SpringApplication.run(BfhQualifierApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		webhookService.process();
	}
}
