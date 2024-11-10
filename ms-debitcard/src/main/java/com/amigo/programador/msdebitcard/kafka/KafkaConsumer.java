package com.amigo.programador.msdebitcard.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;

import com.amigo.programador.msdebitcard.entity.DebitCard;
import com.amigo.programador.msdebitcard.service.DebitCardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KafkaConsumer {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private DebitCardService service;
	
	private static final String TOPIC = "TRANSFERENCE";
	
	@Bean
	public NewTopic topic() {
		return TopicBuilder.name(TOPIC)
				.partitions(10)
				.replicas(1)
				.build();
	}
	
	@KafkaListener(id="consumerKafka", topics = TOPIC)
	public void getMessage(String message) throws JsonMappingException, JsonProcessingException {
		DebitCard debitCard = objectMapper.readValue(message, DebitCard.class);
		System.out.println(">>> Consume DebitCard message");
		service.updateDebitCard(debitCard).subscribe();
		System.out.println(">>> DebitCard updated");
	}
	
}
