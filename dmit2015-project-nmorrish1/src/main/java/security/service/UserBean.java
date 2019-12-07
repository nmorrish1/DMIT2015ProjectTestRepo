package security.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

import org.apache.commons.lang3.time.DateUtils;

import ca.project.entities.CalendarEvent;
import ca.project.mail.SendConfirmationMail;
import ca.project.mail.SendMail;
import security.entities.Role;
import security.entities.User;
import security.entities.VerificationToken;

@Singleton
@Interceptors({InterceptorAdmins.class})
@PermitAll
public class UserBean {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private Pbkdf2PasswordHash passwordHash;
	
	@EJB
	private RoleBean roleBean;
	
	@Resource
	TimerService timerService;
	
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
	
	@RolesAllowed(value = {"ADMIN", "DEVELOPER"})
	public void delete(User existingUser) {
		if(!entityManager.contains(existingUser)) {
			existingUser = entityManager.merge(existingUser);
		}
		
		entityManager.remove(existingUser);
	}
	
	public User findUserById(Long userId) {
		return entityManager.find(User.class, userId);
	}
	
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
	
	public void lock(String userName) {
		User existingUser = findUserByUserName(userName);
		
		if(!entityManager.contains(existingUser)) {
			
			existingUser.setLocked(true);
			
			entityManager.merge(existingUser);
			entityManager.flush();
		}
		
	}
	
	public void verify(Long userId) {
		
		User user = findUserById(userId);
		
		if (user != null) {
			user.setVerified(true);
			
			entityManager.merge(user);
			entityManager.flush();
		}
	}
	
	public void newAnonUser(User newUser) {
		if (findUserByUserName(newUser.getUsername()) != null) {
			throw new RuntimeException("The username " + newUser.getUsername() + " already exists");
		}
		
		String hashedPassword = passwordHash.generate(newUser.getPlainTextPassword().toCharArray()); 
		newUser.setPassword(hashedPassword);
		
		newUser.setVerified(false);
		
		List<Role> roles = new ArrayList<>();
		roles.add(roleBean.findRoleByName("USER") );
		newUser.setRoles(roles);

		entityManager.persist(newUser);
		
		//Assign new user a token
		User user = findUserByUserName(newUser.getUsername());
		VerificationToken token = new VerificationToken();
		token.setUserId(user.getUser_id());
		entityManager.persist(token);

		//Set a timer: If the user does not verify the account in time, their account is deleted
		ScheduleExpression scheduleExpression = new ScheduleExpression();
	    GregorianCalendar calendar = new GregorianCalendar();
	    
	    calendar.setTime(token.getExpiryDate());
	    
		scheduleExpression.year(calendar.get(Calendar.YEAR));
		scheduleExpression.month(calendar.get(Calendar.MONTH) + 1);
		scheduleExpression.dayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
		scheduleExpression.hour(calendar.get(Calendar.HOUR_OF_DAY));
		scheduleExpression.minute(calendar.get(Calendar.MINUTE));
		scheduleExpression.second(0);
		
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(token);
		
	    timerService.createCalendarTimer(scheduleExpression, timerConfig);
	    
	    //Send a message with the confirmation link
	    SendConfirmationMail confirmationMsg = new SendConfirmationMail(newUser, token);
		confirmationMsg.Send();
	}
	
//	@EJB 
//	private TokenBean token;
	
	public VerificationToken findTokenByID(Long tokenId) {
		return entityManager.find(VerificationToken.class, tokenId);
	}
	
	public VerificationToken findToken(String token) {
		VerificationToken queryResult = null;
		
		try {
			queryResult = entityManager.createQuery("SELECT t FROM VerificationToken t WHERE t.token = :tokenValue", VerificationToken.class)
					.setParameter("tokenValue", token)
					.getSingleResult();
		} catch (NoResultException e) {
			queryResult = null;
		}
		
		return queryResult;
	}
	
	@Timeout
	public void sendReminder(Timer timer) {
		
		VerificationToken timerToken = (VerificationToken) timer.getInfo();
		VerificationToken tokenResult = findTokenByID(timerToken.getToken_id());
		User user = findUserById(tokenResult.getUserId());
		
		//by default, user.getVerified for new accounts is false. If the user has not verified, delete their account and associated token
		if(!user.getVerified() && user!= null && tokenResult != null) {
			entityManager.remove(user); 
			//System.out.println("user deleted");
			entityManager.remove(tokenResult);
			//System.out.println("token deleted");
			
		} else if (tokenResult != null) {
			entityManager.remove(tokenResult);
			//System.out.println("token deleted");
		}
		
	}

}
