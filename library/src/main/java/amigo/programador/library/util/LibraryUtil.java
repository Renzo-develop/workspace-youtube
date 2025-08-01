package amigo.programador.library.util;

import amigo.programador.library.model.ApiResponse;
import amigo.programador.library.model.CustomException;
import amigo.programador.library.model.CustomResponse;
import org.springframework.http.HttpStatus;

public class LibraryUtil {

	public static ApiResponse buildApiResponse(String message, Object response) {
		return ApiResponse.builder()
					.message(message)
					.response(response)
					.build();
	}

	public static CustomException buildCustomException(HttpStatus status, Throwable ex) {
		return CustomException.builder()
					.httpStatus(status)
					.response(buildCustomResponse(ex))
					.build();
	}

	public static CustomResponse buildCustomResponse(Throwable ex) {
		return CustomResponse.builder()
					.error(ex.getClass())
					.message(ex.getMessage())
					.build();
	}

}
