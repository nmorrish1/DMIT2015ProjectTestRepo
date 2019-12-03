package security.service;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

@Singleton
//@Interceptors({LoginUserSecurityInterceptor.class})
public class UserBean {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private Pbkdf2PasswordHash passwordHash;

}
