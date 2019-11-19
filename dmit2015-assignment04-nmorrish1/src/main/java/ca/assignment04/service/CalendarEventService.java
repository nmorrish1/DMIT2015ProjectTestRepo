package ca.assignment04.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import ca.assignment04.entities.CalendarEvent;

@Stateless
public class CalendarEventService {
	
	@PersistenceContext(unitName = "persistence-unit-from-persistence-xml", type = PersistenceContextType.TRANSACTION)
	private EntityManager manageStatelessEntities;
	
	public void add(CalendarEvent event) {
		manageStatelessEntities.persist(event);
	}
	
	public void update(CalendarEvent event) {
		manageStatelessEntities.merge(event);
	}
	
	public void remove(CalendarEvent event) {
		manageStatelessEntities.remove(event);
	}
	
	public CalendarEvent findEventById(Integer entityId) {
		return manageStatelessEntities.find(CalendarEvent.class, entityId);
	}
	
	public List<CalendarEvent> listAllEvents(){
		
		return manageStatelessEntities.createQuery(
					"SELECT event FROM CalendarEvent event",
					CalendarEvent.class)
				.getResultList();
	}

}
