package security.web;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.Password;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import lombok.Getter;
import lombok.Setter;
import security.service.UserBean;

@Named
@SessionScoped
public class Login implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int MAX_ATTEMPTS_ALLOWED = 5;

//	@Inject
//	private Logger logger;

	@Getter
	private String displayName = "No Assigned Display Name";

	@Getter
	private Long userId;

	@Getter
	private String firstName = "No First Name";

	@Getter
	private String lastName = "No Last Name";

	@Getter
	private String userEmail = "no email";

	@Getter
	private int loginAttempts = 0;

	@Inject
	private SecurityContext securityContext;

	@EJB
	UserBean userBean;

	@Inject
	@ManagedProperty("#{param.new}")
	private boolean isNew; // added for Caller-Initiated Authentication

	@NotBlank(message = "Username value is required.")
	@Getter
	@Setter
	private String username;

	@NotBlank(message = "Password value is required.")
	@Getter
	@Setter
	private String password;

	public void submit() {
		if (loginAttempts >= MAX_ATTEMPTS_ALLOWED | userBean.findUserByUserName(username).getLocked()) {
			// Faces.redirectPermanent("https://www.nait.ca/programs/dmit-computer-software-development?term=2020-winter");
			Messages.addGlobalError("The account you are attempting to access is locked. Please bother the Administrators so they can unlock it for you.");
			
		} else if (!userBean.findUserByUserName(username).getVerified()) {
			Messages.addGlobalInfo("The account you are attempting to access has not yet been verified. Please check your email or bother the Administrators");
			
		} else {
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
				Messages.addFlashGlobalInfo("Login succeed");
				// queryLdapUserInfo();
				Faces.redirect(Faces.getRequestContextPath() + "/index.xhtml"); // added for Caller-Initiated
																				// Authentication
				break;
			case NOT_DONE:
				// JSF does not need to take any special action here
				break;
			}
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
