package com.amigo.programador.msdebitcard.config;

import com.amigo.programador.library.interceptor.CustomInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CustomInterceptor.class)
public class InterceptorConfiguration {
}
