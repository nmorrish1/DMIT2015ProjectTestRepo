package security.entities;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, User> {

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context) {
		if (user == null) {
			return true;
		}
		
		if ( StringUtils.isBlank(user.getPlainTextPassword()) || StringUtils.isBlank(user.getConfirmedPlainTextPassword()) ) {
			return false;
		}

		return  user.getPlainTextPassword().equals(user.getConfirmedPlainTextPassword());
	}

}
