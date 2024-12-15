package com.amigo.programador.msclient.entity;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class Client {

	@NotNull
	private Long id;

	@Valid
	@NotNull
	@NotBlank(message = "Name is mandatory")
	private String name;

	@NotNull
	private Integer age;
}
