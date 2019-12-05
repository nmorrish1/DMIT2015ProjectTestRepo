package security.data;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

import security.entities.User;
import security.service.UserBean;

public class CustomIdentityStore implements IdentityStore {
	
	@Inject
	private UserBean userBean;
	
	@Inject
	private Pbkdf2PasswordHash passwordHash;
	
	@Override
	public CredentialValidationResult validate(Credential credential) {
		UsernamePasswordCredential loginInfo = (UsernamePasswordCredential) credential;
		
		String userName = loginInfo.getCaller();
		
		User existingUser = userBean.findUserByUserName(userName);
		
		if (existingUser != null && passwordHash.verify(loginInfo.getPasswordAsString().toCharArray(), existingUser.getPassword())) {
			return new CredentialValidationResult(userName, existingUser.getRoles().stream().map(item -> item.getRoleName()).collect(Collectors.toSet()));
		} else {
			return CredentialValidationResult.INVALID_RESULT;
		}
	}

}
