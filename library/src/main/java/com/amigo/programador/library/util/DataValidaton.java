package com.amigo.programador.library.util;

import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import reactor.core.publisher.Mono;

@Slf4j
public class DataValidaton {

  public static Mono<Object> isUniqueValue(String parameter, String methodName, Object repository) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    Method method = repository.getClass().getMethod(methodName, String.class);
    Mono<Object> result = (Mono<Object>) method.invoke(repository, parameter);

    return result
      .flatMap(r -> Mono.error(new IllegalArgumentException("Duplicate value: " + parameter)))
      .switchIfEmpty(Mono.empty());
  }

}
