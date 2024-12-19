package com.amigo.programador.msclient.entity;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.amigo.programador.library.annotation.UniqueField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class Client {

	@NotNull
	private Long id;

	@Valid
	@NotNull
	@NotBlank(message = "Name is mandatory")
	@UniqueField(fieldName = "name", entityClass = Client.class, message = "Client name cannot be register twice")
	private String name;

	@NotNull
	private Integer age;
}
