package com.amigo.programador.library.util;

import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import reactor.core.publisher.Mono;

@Slf4j
public class DataValidaton {

  public static <T> Mono<T> isUniqueValue(String parameter, String methodName, Object repository, Class<T> type) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    Method method = repository.getClass().getMethod(methodName, String.class);
    Mono<T> result = (Mono<T>) method.invoke(repository, parameter);

    return result
      .map(r -> validateDuplicate(r))
      .switchIfEmpty(Mono.empty());
  }

  private static <T> T validateDuplicate(T t) {
    if(Objects.nonNull(t)) {
      throw new IllegalArgumentException("Duplicate value: " + t);
    }
    return null;
  }

}
