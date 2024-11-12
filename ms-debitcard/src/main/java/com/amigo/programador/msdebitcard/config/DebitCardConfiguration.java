package com.amigo.programador.msdebitcard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DebitCardConfiguration {

	@Bean
	public WebClient buildWebClient(@Value("${application.consume.msclient.url}") String url) {
		WebClient webMsClient = WebClient.create(url);
		return webMsClient;
	}

}
