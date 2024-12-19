package com.amigo.programador.msclient.configuration;

import com.amigo.programador.library.util.JacksonUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({JacksonUtil.class})
public class ClientConfiguration {
}
