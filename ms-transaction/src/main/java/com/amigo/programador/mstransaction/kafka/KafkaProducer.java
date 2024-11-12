package com.amigo.programador.mstransaction.kafka;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;

import com.amigo.programador.library.DebitCard;

@Service
public class KafkaProducer {

	private static final String topic = "TRANSFERENCE";

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	public void updateDebitCardBalance(DebitCard debitCard) {

		try {
			System.out.println(">>> Sending DebitCard message");
			String debitCardString = objectMapper.writeValueAsString(debitCard);
			kafkaTemplate.send(topic, debitCardString);
			System.out.println(">>> Sent DebitCard message");
		} catch (JsonProcessingException ex) {
			throw new RuntimeException(ex);
		}

	}

}
