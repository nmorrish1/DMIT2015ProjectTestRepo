package security.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.batch.operations.JobOperator;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.JobStartException;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import security.entities.Role;

@Singleton
@Startup
public class CreateRoles {
		
		@PersistenceContext(unitName = "persistence-unit-from-persistence-xml")
		private EntityManager entityManager;

		@Transactional
		@PostConstruct
		public void process(){
			
			Role role1 = new Role();
			Role role2 = new Role();
			Role role3 = new Role();
			
			try {
				
				role1.setRoleName("USER");
				entityManager.persist(role1);
				
				role2.setRoleName("ADMIN");
				entityManager.persist(role2);
				
				role3.setRoleName("DEVELOPER");
				entityManager.persist(role3);
				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
			
		}
}
