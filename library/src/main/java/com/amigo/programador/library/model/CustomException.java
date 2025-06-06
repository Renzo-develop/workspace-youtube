package com.amigo.programador.library.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class CustomException extends RuntimeException {

	private HttpStatus httpStatus;

	private CustomResponse response;

}
