package security.service;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

import security.entities.Role;
import security.entities.User;
import security.web.SecurityEventInterceptor;

@Singleton
@Interceptors({SecurityEventInterceptor.class})
@PermitAll
public class UserBean {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private Pbkdf2PasswordHash passwordHash;
	
	@EJB
	private RoleBean roleBean;
	
	@Lock(LockType.WRITE)
	@RolesAllowed(value = {"ADMIN", "DEVELOPER"})
	public void add(User newUser, String[] roleNames) {
		
		if (findUserByUserName(newUser.getUsername()) != null) {
			throw new RuntimeException("The username " + newUser.getUsername() + " already exists");
			
		}
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
	
	@RolesAllowed(value = {"ADMIN", "DEVELOPER"})
	public void update(User existingUser, String[] roleNames) {
		if(roleNames != null && roleNames.length > 0) {
			existingUser.getRoles().clear();
			
			for(String singleRole : roleNames) {
				Role userRole = roleBean.findRoleByName(singleRole);
				existingUser.getRoles().add(userRole);
			}
		}
		
		entityManager.merge(existingUser);
		entityManager.flush();
	}
	
	public void delete(User existingUser) {
		if(!entityManager.contains(existingUser)) {
			existingUser = entityManager.merge(existingUser);
		}
		
		entityManager.remove(existingUser);
	}
	
	public User findUserById(Long userId) {
		return entityManager.find(User.class, userId);
	}
	
	@RolesAllowed(value = {"ADMIN", "DEVELOPER"})
	public User findUserByUserName(String userName) {
		User queryResult = null;
		
		try {
			queryResult = entityManager.createQuery("SELECT u FROM User u WHERE upper(u.username) = :usernameValue", User.class)
					.setParameter("usernameValue", userName.toUpperCase())
					.getSingleResult();
		} catch (NoResultException e) {
			queryResult = null;
		}
		
		return queryResult;
	}
	
	@Lock(LockType.READ)
	@RolesAllowed(value = {"ADMIN", "DEVELOPER"})
	public List<User> list(){
		return entityManager.createQuery("SELECT u FROM User u ORDER BY u.username", User.class).getResultList();
	}
	
	@RolesAllowed(value = {"USER", "ADMIN", "DEVELOPER"})
	public void changePassword(String username, String currentPlainTxtPwd, String newPlainTxtPwd) throws Exception {
		char[] currentPassword = currentPlainTxtPwd.toCharArray();
		
		User existingUser = findUserByUserName(username);
		
		if (passwordHash.verify(currentPassword, existingUser.getPassword())) {
			String newHashedPwd = passwordHash.generate(newPlainTxtPwd.toCharArray());
			existingUser.setPassword(newHashedPwd);
			entityManager.merge(existingUser);
		} else {
			throw new Exception ("Current password is incorrect");
		}
	}

}
