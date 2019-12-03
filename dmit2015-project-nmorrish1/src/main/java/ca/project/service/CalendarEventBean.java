package ca.project.service;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.security.enterprise.SecurityContext;

import org.apache.commons.lang3.time.DateUtils;

import ca.project.entities.CalendarEvent;
import ca.project.mail.SendMail;

@Stateless
@Interceptors({EventInterceptor.class})
@PermitAll
public class CalendarEventBean {
	
	@Inject
	private SecurityContext securityContext;
	
	@Inject
	private Logger logger;
	
	@PersistenceContext(unitName = "persistence-unit-from-persistence-xml", type = PersistenceContextType.TRANSACTION)
	private EntityManager manageStatelessEntities;
	
	@Resource
	TimerService timerService;

	
	@Timeout
	public void sendReminder(Timer timer) {
		CalendarEvent event = (CalendarEvent) timer.getInfo();
		SendMail message = new SendMail(event.getReminderEmail(), event);
		message.Send();
		System.out.println("Reminder Sent");
	}
	
	@RolesAllowed(value = {"**"})
	public void add(CalendarEvent event) {
		String username = securityContext.getCallerPrincipal().getName();
		event.setUserId(username);
		
		
		if (event.getReminderNumber() > 0) {
			
			ScheduleExpression scheduleExpression = new ScheduleExpression();

		    GregorianCalendar calendar = new GregorianCalendar();
		    
		    calendar.setTime(DateUtils.addMinutes(event.getStartDate(), -1 * event.getReminderNumber()));
		    
			scheduleExpression.year(calendar.get(Calendar.YEAR));
			scheduleExpression.month(calendar.get(Calendar.MONTH) + 1);
			scheduleExpression.dayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			scheduleExpression.hour(calendar.get(Calendar.HOUR_OF_DAY));
			scheduleExpression.minute(calendar.get(Calendar.MINUTE));
			scheduleExpression.second(0);
			
			TimerConfig timerConfig = new TimerConfig();
			timerConfig.setInfo(event);
			
		    timerService.createCalendarTimer(scheduleExpression, timerConfig);
		}
		
		manageStatelessEntities.persist(event);
		
	}
	
	@RolesAllowed("**")
	public void remove(CalendarEvent event) {
		if (!manageStatelessEntities.contains(event)) {
			event = manageStatelessEntities.merge(event);
		}
		Collection<Timer> activeTimers = timerService.getAllTimers();
		for(Timer currentTimer : activeTimers) {
			CalendarEvent listEvent = (CalendarEvent) currentTimer.getInfo();
			if (listEvent.getEventId().equals(listEvent.getEventId())) {
				currentTimer.cancel();
				break;
			}
		}
		
		manageStatelessEntities.remove(event);
		manageStatelessEntities.flush();
	}
	
	@RolesAllowed("**")
	public CalendarEvent findEventById(Integer entityId) {
		return manageStatelessEntities.find(CalendarEvent.class, entityId);
	}
	
	@RolesAllowed("**")
	public CalendarEvent findById(Long id) {
		return manageStatelessEntities.find(CalendarEvent.class, id);
	}
	
	@RolesAllowed("**")
	public void update(CalendarEvent event) {

		
		Collection<Timer> activeTimers = timerService.getAllTimers();
		for(Timer currentTimer : activeTimers) {
			CalendarEvent listEvent = (CalendarEvent) currentTimer.getInfo();
			if (listEvent.getEventId().equals(listEvent.getEventId())) {
				currentTimer.cancel();
				break;
			}
		}
		
		if (event.getReminderNumber() > 0) {
					
			ScheduleExpression scheduleExpression = new ScheduleExpression();

		    GregorianCalendar calendar = new GregorianCalendar();
		    
		    calendar.setTime(DateUtils.addMinutes(event.getStartDate(), -1 * event.getReminderNumber()));
		    
			scheduleExpression.year(calendar.get(Calendar.YEAR));
			scheduleExpression.month(calendar.get(Calendar.MONTH) + 1);
			scheduleExpression.dayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			scheduleExpression.hour(calendar.get(Calendar.HOUR_OF_DAY));
			scheduleExpression.minute(calendar.get(Calendar.MINUTE));
			scheduleExpression.second(0);
			
			TimerConfig timerConfig = new TimerConfig();
			timerConfig.setInfo(event);
			
		    timerService.createCalendarTimer(scheduleExpression, timerConfig);
		}
		
		manageStatelessEntities.merge(event);
		manageStatelessEntities.flush();
		
	}
	
	//@RolesAllowed("**")
	public List<CalendarEvent> listAllEvents(){
		
		if(securityContext.getCallerPrincipal() != null) {
			
			String username = securityContext.getCallerPrincipal().getName();
			
			return manageStatelessEntities.createQuery(
					"SELECT event FROM CalendarEvent event WHERE event.username = :usernameValue ORDER BY event.startDate DESC",
					CalendarEvent.class)
				.setParameter("usernameValue", username)
				.getResultList();
			
		} else {
			return manageStatelessEntities.createQuery(
					"SELECT event FROM CalendarEvent event ORDER BY event.startDate DESC",
					CalendarEvent.class)
				.getResultList();
		}
		
		
	}

}
