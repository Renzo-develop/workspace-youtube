package com.amigo.programador.library.annotation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class UniqueFieldValidator implements ConstraintValidator<UniqueField, String> {

  @Autowired
  private ReactiveMongoTemplate mongoTemplate;

  private String fieldName;
  private Class<?> entityClass;

  @Override
  public void initialize(UniqueField constraintAnnotation) {
    this.fieldName = constraintAnnotation.fieldName();
    this.entityClass = constraintAnnotation.entityClass();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if(Objects.isNull(value)) {
      return true;
    }

    Query query = new Query();
    query.addCriteria(Criteria.where(fieldName).is(value));

    Mono<Boolean> exists = mongoTemplate.exists(query, entityClass);

    return !exists.block();
  }
}
