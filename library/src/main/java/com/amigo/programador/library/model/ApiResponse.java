package com.amigo.programador.library.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ApiResponse {

	private String message;

	private Object response;

}