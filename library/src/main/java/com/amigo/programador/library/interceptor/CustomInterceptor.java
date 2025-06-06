package com.amigo.programador.library.interceptor;

import com.amigo.programador.library.model.CustomException;
import com.amigo.programador.library.model.CustomResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.amigo.programador.library.util.LibraryUtil.buildCustomException;
import static com.amigo.programador.library.util.LibraryUtil.buildCustomResponse;

@Slf4j
@RestControllerAdvice
public class CustomInterceptor {

	@ExceptionHandler({CustomException.class})
	protected ResponseEntity<CustomResponse> handleCustomException(CustomException ex) {
		log.error(String.format("Interceptor CustomException: %s", ex));
		return ResponseEntity.status(ex.getHttpStatus()).body(ex.getResponse());
	}

	@ExceptionHandler({Throwable.class})
	protected ResponseEntity<CustomResponse> handleAnotherException(Throwable ex) {
		log.error(String.format("Interceptor AnotherException: %s", ex));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildCustomResponse(ex));
	}

}
