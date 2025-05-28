package com.amigo.programador.library.interceptor;

import com.amigo.programador.library.model.CustomException;
import com.amigo.programador.library.model.CustomResponse;
import com.amigo.programador.library.util.LibraryUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.amigo.programador.library.util.LibraryUtil.buildCustomResponse;

@RestControllerAdvice
public class CustomInterceptor {

	@ExceptionHandler({CustomException.class})
	protected ResponseEntity<CustomResponse> handleCustomException(CustomException ex) {
		return ResponseEntity.status(ex.getHttpStatus()).body(ex.getResponse());
	}

	@ExceptionHandler({Throwable.class})
	protected <T> ResponseEntity<CustomResponse> handleDefaultHandlerExceptionResolver(T ex) {
		Throwable th = (Throwable) ex;
 		buildCustomResponse(th);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildCustomResponse(th));
	}

	@ExceptionHandler({NullPointerException.class})
	protected ResponseEntity<CustomResponse> handleCustomException(NullPointerException ex) {
		return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(buildCustomResponse(ex));
	}

}
