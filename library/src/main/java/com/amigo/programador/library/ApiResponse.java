package com.amigo.programador.library;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiResponse {
	private String message;
	private Object response;
}
