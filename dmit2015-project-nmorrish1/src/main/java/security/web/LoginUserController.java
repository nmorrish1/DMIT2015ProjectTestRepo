package security.web;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;
import javax.validation.constraints.NotBlank;

import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import lombok.Getter;
import lombok.Setter;
import security.entities.Role;
import security.entities.User;
import security.service.RoleBean;
import security.service.UserBean;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Named
@ViewScoped
public class LoginUserController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject 
	private Logger logger;
	
	@Inject
	private SecurityContext securityContext;
	
	@EJB
	private UserBean userBean;
	
	@EJB 
	private RoleBean roleBean;
	
	@Produces
	@Named
	private User userDetails = new User();
	
	@Produces
	@Named
	private Role roleDetail = new Role();
	
	@Getter @Setter
	@NotBlank(message="A user must be assigned to at least one role")
	private String selectedRoles;
	
	@Getter
	private List<Role> userRoles;
	
	@Getter 
	private List<User> users;
	
	@Getter @Setter
	private Long editId;
	
	@Getter @Setter
	private boolean editMode = false;
	
	@PostConstruct
	public void init() {
		try {
			
//			if(securityContext.isCallerInRole("ADMIN")) {
//				System.out.println("invalid access");
//			}
			
			users = userBean.list();
			
			userRoles = roleBean.list();
		} catch (EJBAccessException e) {
			Messages.addGlobalInfo(e.getMessage());
			Messages.addFlashGlobalInfo(e.getMessage());
			
			logger.warning("Access Exception: " + e.getMessage());
			String message = String.format("Remote IP: %s\n Remote User: %s\n", Faces.getRemoteAddr(), Faces.getRemoteUser());			
			logger.warning(message);
		} catch (Exception e) {
			Messages.addGlobalInfo("Retrieve list of {0} was not successful.", User.class.getSimpleName());
		}
	}
	
	public String create() {
		
		String outcome = null;
		
		try {
			String[] groups = selectedRoles.split(",");
			userBean.add(userDetails, groups);
			userDetails = new User();
			Messages.addFlashGlobalInfo("Create new {0} was successful", User.class.getSimpleName());
			outcome = "list?faces-redirect=true";
			
		} catch (EJBAccessException e) {
			Messages.addGlobalInfo(e.getMessage());
			logger.warning("Access Exception: " + e.getMessage());
			String message = String.format("Remote IP: %s\n Remote User: %s\n", Faces.getRemoteAddr(), Faces.getRemoteUser());			
			logger.warning(message);
		} catch (RuntimeException e) {
			Messages.addGlobalError("Error: {0}",  e.getLocalizedMessage());
			
		} catch (Exception e) {
			Messages.addGlobalError("Create new {0} was not successful.", User.class.getSimpleName());
		}
		
		return outcome;
	}
	
	public String update() {
		String outcome = null;
		
		try {
			String[] groups = selectedRoles.split(",");
			userBean.update(userDetails, groups);
			userDetails = new User();
			editMode = false;
			editId = null;
			Messages.addFlashGlobalInfo("Update existing {0} was successful", User.class.getSimpleName());
			outcome = "list?faces-redirect=true";
			
		} catch(EJBAccessException e) {
			Messages.addGlobalInfo(e.getMessage());
			logger.warning("Access Exception: " + e.getMessage());
			String message = String.format("Remote IP: %s\n Remote User: %s\n", Faces.getRemoteAddr(), Faces.getRemoteUser());			
			logger.warning(message);	
		} catch(Exception e) {
			Messages.addGlobalError("Update existing {0} was not successful.", User.class.getSimpleName());
		}
		
		return outcome;
	}
	
	public String delete() {
		String outcome = null;
		
		try {
			users.remove(userDetails);
			userBean.delete(userDetails);
			userDetails = null;
			editMode = false;
			editId = null;
			Messages.addFlashGlobalInfo("Update existing {0} was successful", User.class.getSimpleName());
			outcome = "list?faces-redirect=true";
			Faces.redirect("ListEvent.xhtml");
			
		} catch(EJBAccessException e) {
			Messages.addGlobalInfo(e.getMessage());
			logger.warning("Access Exception: " + e.getMessage());
			String message = String.format("Remote IP: %s\n Remote User: %s\n", Faces.getRemoteAddr(), Faces.getRemoteUser());			
			logger.warning(message);	
		} catch(Exception e) {
			Messages.addGlobalInfo("Update existing {0} was not successful.", User.class.getSimpleName());
		}
		
		return outcome;
	}
	
	public void edit() {
		if(!Faces.isPostback() && !Faces.isValidationFailed()) {
			if(editId != null) {
				try {
					userDetails = userBean.findUserById(editId);
					if(userDetails != null) {
						editMode = true;
						selectedRoles = "";
						for(Role role : userDetails.getRoles()) {
							selectedRoles += role.getRoleName() + ",";
						}
					}
					
				} catch (Exception e) {
					Messages.addGlobalInfo("Query by id was not successful");
				}	
			} else {
				Faces.navigate("list?faces-redirect=true");
			}
		}
	}
	
	public String cancel() {
		userDetails = null;
		editMode = false;
		Faces.redirect("ListEvent.xhtml");
		return "list?faces-redirect=true";
	}
	
	public String newUser() {
		String outcome = null;
		
		try {
			userBean.newAnonUser(userDetails);
			userDetails = new User();
			Messages.addFlashGlobalInfo("The account {0} was successful created. Please check your email to verify your account", User.class.getSimpleName());
			outcome = "list?faces-redirect=true";
			
		} catch (EJBAccessException e) {
			Messages.addGlobalInfo(e.getMessage());
			logger.warning("Access Exception: " + e.getMessage());
			String message = String.format("Remote IP: %s\n Remote User: %s\n", Faces.getRemoteAddr(), Faces.getRemoteUser());			
			logger.warning(message);
		} catch (RuntimeException e) {
			Messages.addGlobalError("Error: {0}",  e.getLocalizedMessage());
			
		} catch (Exception e) {
			Messages.addGlobalError("Create new {0} was not successful.", User.class.getSimpleName());
		}
		
		return outcome;
	}
	
	@Getter @Setter
	@NotBlank(message="Current Password field value is required")
	private String currentPassword;
	
	private String changePassword() {
		String nextUrl = null;
		try {
			if (userDetails.getPlainTextPassword().equals(userDetails.getConfirmedPlainTextPassword())) {
				String currentUsername = Faces.getRemoteUser();
				userBean.changePassword(currentUsername, currentPassword, userDetails.getPlainTextPassword());
				Messages.addFlashGlobalInfo("Change password was successful.");
				nextUrl = "/index?faces-redirect=true";				
			} else {
				Messages.addGlobalInfo("New password must match Confirmed new password.");
			}
		} catch(EJBAccessException e) {
			Messages.addGlobalInfo(e.getMessage());
			logger.warning("Access Exception: " + e.getMessage());
			String message = String.format("Remote IP: %s\n Remote User: %s\n", Faces.getRemoteAddr(), Faces.getRemoteUser());			
			logger.warning(message);
		} catch(Exception e) {
			Messages.addGlobalInfo(e.getMessage());
		}
		return nextUrl;
	}
	

}
