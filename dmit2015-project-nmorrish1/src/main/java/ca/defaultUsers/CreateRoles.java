package ca.defaultUsers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.batch.operations.JobOperator;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import security.entities.Role;
import security.entities.User;
import security.service.UserBean;

@Singleton
@Startup
public class CreateRoles {
		
		@PersistenceContext(unitName = "persistence-unit-from-persistence-xml")
		private EntityManager entityManager;
		
		@EJB
		CreateUserBean userBean;

		@Transactional
		@PostConstruct
		public void process(){
			
			Role role1 = new Role();
			Role role2 = new Role();
			Role role3 = new Role();
			
			User userDetails = new User();
			String[] userRoles = new String[] {"USER","ADMIN","DEVELOPER"};
			
			try {
				
				role1.setRoleName("USER");
				entityManager.persist(role1);
				
				role2.setRoleName("ADMIN");
				entityManager.persist(role2);
				
				role3.setRoleName("DEVELOPER");
				entityManager.persist(role3);
				
				userDetails.setUsername("user2015");
				userDetails.setEmail("nmorrish@ualberta.ca");
				userDetails.setVerified(true);
				userDetails.setPlainTextPassword("Password2015");
				userDetails.setConfirmedPlainTextPassword("Password2015");
				
				userBean.add(userDetails, userRoles);
				
				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
			
		}
}
