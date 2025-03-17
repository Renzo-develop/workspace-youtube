package com.amigo.programador.mstransaction.config;

import com.amigo.programador.library.util.JacksonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Import({JacksonUtil.class})
public class DebitCardConfiguration {

	@Bean
	public WebClient buildWebClient(@Value("${application.consume.msdebitcard.url}") String url) {
		WebClient webMsClient = WebClient.create(url);
		return webMsClient;
	}

}
