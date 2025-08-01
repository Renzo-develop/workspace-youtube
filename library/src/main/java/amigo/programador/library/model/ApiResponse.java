package amigo.programador.library.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@Jacksonized
public class ApiResponse {
	private String message;
	private Object response;
}
