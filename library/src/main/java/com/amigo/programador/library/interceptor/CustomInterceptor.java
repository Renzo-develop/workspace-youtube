package com.amigo.programador.library.interceptor;

import com.amigo.programador.library.model.CustomException;
import com.amigo.programador.library.model.CustomExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@RestControllerAdvice
public class CustomInterceptor {

	@ExceptionHandler({CustomException.class})
	protected ResponseEntity<CustomExceptionResponse> handleCustomException(CustomException ex) {
		return ResponseEntity.status(ex.getHttpStatus()).body(ex.getResponse());
	}

	@ExceptionHandler({DefaultHandlerExceptionResolver.class})
	protected ResponseEntity<CustomExceptionResponse> handleDefaultHandlerExceptionResolver(DefaultHandlerExceptionResolver ex) {
		return ResponseEntity.status(ex.getHttpStatus()).body(ex.getResponse());
	}

}
