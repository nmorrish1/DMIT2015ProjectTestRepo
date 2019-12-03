package security.service;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
//@Interceptors({LoginGroupSecurityInterceptor.class})
public class RoleBean {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Lock(LockType.WRITE)
	public void add(String groupname) {
		
	}

}
