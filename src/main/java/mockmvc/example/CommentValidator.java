package mockmvc.example;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import global.Log;

public class CommentValidator implements Validator {
	public CommentValidator() {
		Log.append("CV ");
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return false;
	}

	@Override
	public void validate(Object target, Errors errors) {
	}
}
