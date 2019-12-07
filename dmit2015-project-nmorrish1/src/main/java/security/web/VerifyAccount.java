package security.web;

import javax.ejb.EJB;
import javax.faces.annotation.ManagedProperty;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.Password;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import lombok.Getter;
import lombok.Setter;
import security.entities.User;
import security.entities.VerificationToken;
import security.service.TokenBean;
import security.service.UserBean;

import java.io.Serializable;
import java.util.logging.Logger;

@Named
@ViewScoped
public class VerifyAccount implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final int MAX_ATTEMPTS_ALLOWED = 5;

	@Getter @Setter
	private String verificationKey = null;
	
	@Getter
	private int loginAttempts = 0;

	@EJB
	UserBean userBean;
	
	@NotBlank(message = "Username value is required.")
	@Getter @Setter
	private String username;

	@NotBlank(message = "Password value is required.")
	@Getter @Setter
	private String password;
	
	@Inject
	@ManagedProperty("#{param.new}")
	private boolean isNew; // added for Caller-Initiated Authentication

	@Inject
	Logger logger;
	
	@Inject
	private SecurityContext securityContext;

	private VerificationToken token = null;
	
	private User user;

	public void init() {
		try {
			if (verificationKey == null) {
				Faces.redirectPermanent(Faces.getRequestContextPath() + "/index.xhtml");
			}
		} catch (Exception e) {
			Messages.addGlobalError("ERROR: {0}", e.getMessage());
		}
		

	}

	public void verify() {
		
		token = userBean.findToken(verificationKey);
		
		if (token != null) {
			try {
				switch (continueAuthentication()) {
				case SEND_CONTINUE:
					// queryLdapUserInfo();
					Faces.responseComplete();

					break;
				case SEND_FAILURE:
					loginAttempts += 1;
					Messages.addGlobalError("Login failed. Incorrect login credentials.");
					Messages.addGlobalError("Login attempt #{0}", loginAttempts);
					if (loginAttempts >= MAX_ATTEMPTS_ALLOWED) {
						Messages.addGlobalFatal("You {0} are banned from this site", username);
						userBean.lock(username);
					}
					break;
				case SUCCESS:
					Faces.getFlash().setKeepMessages(true);
					
					//Get token information and compare it to info in db
					user = userBean.findUserById(token.getUserId());
					
					if(user.getUser_id() == token.getUserId()) {
						userBean.verify(token.getUserId());
						Messages.addFlashGlobalInfo("Account Successfully Verified");
					} else {
						userBean.lock(securityContext.getCallerPrincipal().getName());
						Messages.addGlobalError("Mismatch between token and user, your account has been locked!");
					}
					
					
					
					//Faces.redirect(Faces.getRequestContextPath() + "/index.xhtml"); 
					break;
				case NOT_DONE:
					// JSF does not need to take any special action here
					break;
				}

			} catch (Exception e) {
				Messages.addGlobalError("Query unsucessful");
				logger.fine(e.getMessage());
			}

		} else {
			Messages.addGlobalError("The activation url you have used is invalid or expired.");
		}

	}
	
	private AuthenticationStatus continueAuthentication() {
		Credential credential = new UsernamePasswordCredential(username, new Password(password));
		HttpServletRequest request = Faces.getRequest();
		HttpServletResponse response = Faces.getResponse();
		return securityContext.authenticate(request, response,
				AuthenticationParameters.withParams().newAuthentication(isNew) // added for Caller-Initiated
																				// Authentication
						.credential(credential));
	}
}
