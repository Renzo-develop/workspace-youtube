package amigo.programador.library.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomResponse {

	private Class error;

	private String message;

}
