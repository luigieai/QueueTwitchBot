package br.dev.luigi.twitchqueue;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TwitchqueueApplication {

	public static final Logger log = LoggerFactory.getLogger(TwitchqueueApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TwitchqueueApplication.class, args);
	}

}
