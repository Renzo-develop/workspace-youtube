package com.amigo.programador.library.util;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.CustomException;
import com.amigo.programador.library.model.CustomExceptionResponse;
import org.springframework.http.HttpStatus;

import java.util.Objects;

public class LibraryUtil {

	public static String getRootCause(Throwable throwable) {
		Objects.requireNonNull(throwable);
		Throwable rootCause = throwable;
		while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
			rootCause = rootCause.getCause();
		}
		return rootCause.getClass().getCanonicalName();
	}

	public static ApiResponse buildApiResponse(String message, Object response) {
		return ApiResponse.builder()
			.message(message)
			.response(response)
			.build();
	}

	public static CustomException buildCustomException(HttpStatus status, Throwable throwable) {
		return CustomException.builder()
					.status(HttpStatus.CONFLICT)
					.response(CustomExceptionResponse.builder()
						.error(getRootCause(throwable))
						.message(throwable.getMessage())
						.build())
					.build();
	}
}
