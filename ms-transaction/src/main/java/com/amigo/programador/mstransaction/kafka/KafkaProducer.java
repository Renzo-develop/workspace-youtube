package com.amigo.programador.mstransaction.kafka;


import com.amigo.programador.library.model.DebitCard;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class KafkaProducer {

	private static final String topic = "TRANSFERENCE";

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	public void updateDebitCardBalance(DebitCard debitCard) {

		try {
			log.debug("Sending DebitCard {} to the Kafka topic", debitCard);
			String debitCardString = objectMapper.writeValueAsString(debitCard);
			kafkaTemplate.send(topic, debitCardString);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException(ex);
		}

	}

}
