package com.amigo.programador.library.util;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.CustomException;
import com.amigo.programador.library.model.CustomResponse;
import org.springframework.http.HttpStatus;

import java.util.Objects;

public class LibraryUtil {

	public static ApiResponse buildApiResponse(String message, Object response) {
		return ApiResponse.builder()
						.message(message)
						.response(response)
						.build();
	}

	public static CustomException buildCustomException(HttpStatus status, Throwable exception) {
		var root = getRootException(exception);

		return CustomException.builder()
			.httpStatus(status)
			.response(buildCustomResponse(root))
			.build();
	}

	public static CustomResponse buildCustomResponse(Throwable root) {
		return CustomResponse
			.builder()
			.exception(root.getClass())
			.message(root.getMessage())
			.build();
	}

	private static Throwable getRootException(Throwable throwable) {
		Throwable root = throwable;

		while (Objects.nonNull(root.getCause()) && root.getCause() != root) {
			root = root.getCause();
		}

		return root;
	}

}
