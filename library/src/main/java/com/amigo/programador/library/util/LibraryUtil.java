package com.amigo.programador.library.util;

import com.amigo.programador.library.model.ApiResponse;

public class LibraryUtil {

	public static ApiResponse buildApiResponse(String message, Object response) {
		return ApiResponse.builder()
						.message(message)
						.response(response)
						.build();
	}

}
