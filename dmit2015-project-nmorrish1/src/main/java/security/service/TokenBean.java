package security.service;

import javax.annotation.security.PermitAll;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import security.entities.User;
import security.entities.VerificationToken;

@Singleton
@Interceptors({InterceptorAdmins.class})
@PermitAll
public class TokenBean {
	
	@PersistenceContext
	private EntityManager entityManager;
	
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

}
