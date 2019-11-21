package ca.assignment04.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.commons.lang3.time.DateUtils;

import ca.assignment04.entities.CalendarEvent;
import ca.assignment04.mail.SendMail;

@Stateless
@DeclareRoles({"ADMIN", "USER"})
@PermitAll
public class CalendarEventService {
	
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
	
	@RolesAllowed({"ADMIN", "USER"})
	public void add(CalendarEvent event) {
		
		
		
		if (event.getReminderNumber() > 0) {
			
			
			ScheduleExpression scheduleExpression = new ScheduleExpression();

		    GregorianCalendar calendar = new GregorianCalendar();
		    
//		    calendar.setTime(DateUtils.addMinutes(event.getStartDate(), event.getReminderNumber()));
		    calendar.setTime(event.getStartDate());
		    
			scheduleExpression.year(calendar.get(Calendar.YEAR));
			scheduleExpression.month(calendar.get(Calendar.MONTH));
			scheduleExpression.dayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			scheduleExpression.hour(calendar.get(Calendar.HOUR_OF_DAY));
			scheduleExpression.minute(calendar.get(Calendar.MINUTE));
			
			TimerConfig timerConfig = new TimerConfig();
			timerConfig.setInfo(event);
			
//			timerService.createSingleActionTimer(event.getReminderNumber(), timerConfig);
		    timerService.createCalendarTimer(scheduleExpression, timerConfig);
		    
//		    SendMail message = new SendMail(event.getReminderEmail(), event);
//			message.Send();

			
		}
		
		manageStatelessEntities.persist(event);
		
	}
	
	@RolesAllowed("ADMIN")
	public void remove(CalendarEvent event) {
		if (!manageStatelessEntities.contains(event)) {
			event = manageStatelessEntities.merge(event);
		}
		manageStatelessEntities.remove(event);
		manageStatelessEntities.flush();
	}
	
	@RolesAllowed({"ADMIN", "USER"})
	public CalendarEvent findEventById(Integer entityId) {
		return manageStatelessEntities.find(CalendarEvent.class, entityId);
	}
	
	@RolesAllowed({"ADMIN", "USER"})
	public CalendarEvent findById(Long id) {
		return manageStatelessEntities.find(CalendarEvent.class, id);
	}
	
	@RolesAllowed({"ADMIN", "USER"})
	public void update(CalendarEvent event) {
		manageStatelessEntities.merge(event);
		manageStatelessEntities.flush();
	}
	
	public List<CalendarEvent> listAllEvents(){
		
		return manageStatelessEntities.createQuery(
					"SELECT event FROM CalendarEvent event",
					CalendarEvent.class)
				.getResultList();
	}

}
