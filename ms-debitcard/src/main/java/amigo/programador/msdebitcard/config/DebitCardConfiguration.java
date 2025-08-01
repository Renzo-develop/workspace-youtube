package amigo.programador.msdebitcard.config;

import amigo.programador.library.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Import({JacksonUtil.class})
public class DebitCardConfiguration {

	@Bean
	public WebClient buildWebClient(@Value("${spring.application.consume.msclient.url}") String url) {
		WebClient webMsClient = WebClient.create(url);
		return webMsClient;
	}


}
