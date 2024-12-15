package com.amigo.programador.msclient.configuration;

import com.amigo.programador.library.interceptor.CustomInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CustomInterceptor.class)
public class InterceptorConfiguration {
}
