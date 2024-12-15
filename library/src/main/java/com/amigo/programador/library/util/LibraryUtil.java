package com.amigo.programador.library.util;

import java.util.Objects;

public class LibraryUtil {

	public static String getRootCause(Throwable throwable) {
		Objects.requireNonNull(throwable);
		Throwable rootCause = throwable;
		while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
			rootCause = rootCause.getCause();
		}
		return rootCause.getClass().getCanonicalName();
	}
}
