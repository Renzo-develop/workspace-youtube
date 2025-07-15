package amigo.programador.mstransaction.kafka;


import amigo.programador.library.model.DebitCard;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


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
