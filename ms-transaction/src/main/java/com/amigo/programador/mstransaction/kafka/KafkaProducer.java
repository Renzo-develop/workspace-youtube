package com.amigo.programador.mstransaction.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;

import com.amigo.programador.library.DebitCard;

@Service
public class KafkaProducer {
	
	private static final String topic = "TRANSFERENCE";
	
	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	private KafkaProducer() {
		Map<String, Object> config = new HashMap<>();
		
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		
		kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(config));
	}
	
	public void updateDebitCardBalance(DebitCard debitCard) {
		System.out.println(">>> Sending DebitCard message");
		kafkaTemplate.send(topic, debitCard);
		System.out.println(">>> Sent DebitCard message");
	}

}
