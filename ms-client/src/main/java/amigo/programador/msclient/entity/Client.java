package amigo.programador.msclient.entity;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class Client {
	
	@NotNull
	private Long id;

	@NotNull
	private String name;

	@NotNull
	private String dni;

	@NotNull
	private Integer age;
}
