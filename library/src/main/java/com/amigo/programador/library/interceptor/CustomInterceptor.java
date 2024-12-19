package com.amigo.programador.library.interceptor;

import com.amigo.programador.library.model.CustomException;
import com.amigo.programador.library.model.CustomExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

import static com.amigo.programador.library.util.LibraryUtil.getRootCause;

@RestControllerAdvice
public class CustomInterceptor {


	@ExceptionHandler({CustomException.class})
	protected ResponseEntity<CustomExceptionResponse> handleCustomException(CustomException ex) {
		var ce = (CustomException)ex;
		return ResponseEntity.status(ce.getStatus()).body(ce.getResponse());
	}

	@ExceptionHandler({MethodArgumentNotValidException.class,
			DateTimeParseException.class,
			HttpMessageNotReadableException.class})
	protected ResponseEntity<CustomExceptionResponse> handleBadRequest(Throwable ex) {
		var ce = CustomException.builder()
						.status(HttpStatus.BAD_REQUEST)
						.response(CustomExceptionResponse.builder()
										.message(ex.getMessage())
										.error(getRootCause(ex))
										.build())
						.build();
		return ResponseEntity.status(ce.getStatus()).body(ce.getResponse());
	}

}
