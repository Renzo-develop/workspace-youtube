package com.amigo.programador.library.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import reactor.core.publisher.Mono;

public class ValidateUtil {

  public static <T> Mono<T> validateDuplicate(String value, String methodName, Object repository, Class<T> returnType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = repository.getClass().getMethod(methodName, String.class);
    Mono<T> result = (Mono<T>) method.invoke(repository, value); // Busque en el repository si existe ese valor

    return result.map(r -> isUnique(r))
      .switchIfEmpty(Mono.empty()); // No existe, no esta duplicado
  }


  private static <T> T isUnique(T response) {
    if(Objects.isNull(response)) { // No es duplicado
      return null;
    }
    throw new IllegalArgumentException("Duplicate value"); // Existe el valor = Duplicado
  }
}
