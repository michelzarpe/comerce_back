package com.michelzarpelon.cursomcmz.resources.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError {
	private static final long serialVersionUID = 1L;

	private List<FieldMessage> errors = new ArrayList<>();



	public ValidationError(Long timestamp, Integer status, String error, String message, String path) {
		super(timestamp, status, error, message, path);
	}

	/*modificando depois do get, muda tambem o nome do parametro no Json*/
	public List<FieldMessage> getErrors() {
		return errors;
	}

	public void addError(String fildName, String messagem) {
		errors.add(new FieldMessage(fildName, messagem));
	}

}
