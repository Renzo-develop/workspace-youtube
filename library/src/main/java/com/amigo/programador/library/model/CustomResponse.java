package com.amigo.programador.library.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomResponse {

	private Class exception;

	private String message;

}
