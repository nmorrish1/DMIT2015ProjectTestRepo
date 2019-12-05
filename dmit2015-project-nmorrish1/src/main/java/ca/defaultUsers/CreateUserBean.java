package ca.defaultUsers;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

import security.entities.Role;
import security.entities.User;
import security.service.RoleBean;

@Singleton
public class CreateUserBean {
	

	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private Pbkdf2PasswordHash passwordHash;
	
	@EJB
	private RoleBean roleBean;
	
	@Lock(LockType.WRITE)
	//@RolesAllowed(value = {"ADMIN", "DEVELOPER"})
	public void add(User newUser, String[] roleNames) {
		String hashedPassword = passwordHash.generate(newUser.getPlainTextPassword().toCharArray()); 
		newUser.setPassword(hashedPassword);
		
		if(roleNames != null && roleNames.length > 0) {
			for (String singleRole : roleNames) {
				Role userRole = roleBean.findRoleByName(singleRole); 
				newUser.getRoles().add(userRole);
			}
		}
		
		entityManager.persist(newUser);
	}

}
