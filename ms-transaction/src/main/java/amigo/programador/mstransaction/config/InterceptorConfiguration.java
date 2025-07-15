package amigo.programador.mstransaction.config;

import amigo.programador.library.interceptor.CustomInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CustomInterceptor.class)
public class InterceptorConfiguration {
}
