package com.amigo.programador.library.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueFieldValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueField {

  String fieldName();

  Class<?> entityClass();

  String message();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
