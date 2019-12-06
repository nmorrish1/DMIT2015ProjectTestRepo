package security.service;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import security.entities.Role;


@Singleton
@Interceptors({InterceptorAdmins.class})
@PermitAll
public class RoleBean {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Lock(LockType.WRITE)
	@RolesAllowed(value = {"ADMIN", "DEVELOPER"})
	public void add(String roleName) {
		
		if(findRoleByName(roleName) != null) {
			throw new RuntimeException("The group name " + roleName + " already exists");
		}
		Role newRole = new Role();
		newRole.setRoleName(roleName);
		entityManager.persist(newRole);
	}
	
	@RolesAllowed(value = {"ADMIN", "DEVELOPER"})
	public void update(Role existingRole) {
		entityManager.merge(existingRole);
		entityManager.flush();
	}
	
	@RolesAllowed(value = {"ADMIN", "DEVELOPER"})
	public void delete(Role existingRole) {
		if (entityManager.contains(existingRole)) {
			existingRole = entityManager.merge(existingRole);
			entityManager.remove(existingRole);
		}
	}
	
	public Role findRoleById(Long roleId) {
		
		return entityManager.find(Role.class, roleId);
	}
	
	public Role findRoleByName(String roleName) {
		Role queryResult = null;
		
		try {
			queryResult = entityManager.createQuery(
					"SELECT r FROM Role r WHERE r.roleName = :roleNameVale "
					, Role.class)
					.setParameter("roleNameVale", roleName)
					.getSingleResult();
		} catch (NoResultException e) {
			queryResult = null;
		}
		
		return queryResult;
	}
	
	@Lock(LockType.READ)
	public List<Role> list(){
		return entityManager.createQuery("SELECT r FROM Role r ORDER BY r.roleName", Role.class).getResultList();
	}

}
