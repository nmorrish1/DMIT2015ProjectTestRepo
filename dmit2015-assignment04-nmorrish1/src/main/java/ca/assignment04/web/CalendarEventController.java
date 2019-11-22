package ca.assignment04.web;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import org.apache.commons.lang3.time.DateUtils;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import ca.assignment04.entities.CalendarEvent;
import ca.assignment04.service.CalendarEventService;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class CalendarEventController implements Serializable{
	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;
	
	@Inject
	private CalendarEventService eventService;
	
	@Getter
	private List<CalendarEvent> events;
	
	@Getter @Setter
	@Valid
	private CalendarEvent currentEvent = new CalendarEvent();
	
	@Getter @Setter
	private String reminderEmail = null;
	
	@Getter @Setter 
	private Long editId = null;
	
	@Getter @Setter 
	private boolean editMode = false;
	
	//Change back to true if email panel ajax is working
	@Getter @Setter
	private boolean hideEmailEntry = false;
	
	@PostConstruct
	void init() {
		try {
			events = eventService.listAllEvents();
		} catch (Exception e) {
			Messages.addGlobalError("Error loading events");
			logger.fine(e.getMessage());
		}
	}
	
	public String create() {
		String outcome = null;
		
		try {
			Date currentDate = new Date(System.currentTimeMillis());
			
			if(		currentDate.compareTo(DateUtils.addMinutes(currentEvent.getStartDate(), -1 * currentEvent.getReminderNumber())) > 0
					&& currentEvent.getReminderNumber() > 0) {
				
				Messages.addGlobalError("Error: the reminder will take place in the past");
				
				
			} else if(currentEvent.getReminderNumber() > 0 && currentEvent.getReminderEmail().isEmpty()) {
				Messages.addGlobalError("Error: please enter an email address to send the reminder to");
				
			} else if (currentEvent.getStartDate().compareTo(currentEvent.getEndDate()) <= 0 ) {
				eventService.add(currentEvent);
				currentEvent = new CalendarEvent();
				Messages.addFlashGlobalInfo("Event successfully added to calendar");
				outcome = "list?faces-redirect=true";
				
				events = eventService.listAllEvents();
			} else {
				Messages.addGlobalError("Event end date cannot come before event start date");
			}
	
			
		} catch (Exception e) {
			Messages.addGlobalError("Error: could not add event");
			logger.fine(e.getMessage());
		}
		
		return outcome;
	}
	
	public void edit() {
		if (!Faces.isPostback() && !Faces.isValidationFailed() ) {
			if (editId != null) {
				try {
					currentEvent = eventService.findById(editId);
					if (currentEvent != null) {
						editMode = true;
					} else {
						Faces.redirect("ListEvent.xhtml");				
					}
				} catch (Exception e) {
					Messages.addGlobalError("Query unsucessful");
					logger.fine(e.getMessage());	
				}	
			} else {
				Faces.navigate("list?faces-redirect=true");	
			}
		} 
	}
	
	public String update() {
		String outcome = null;
		Date currentDate = new Date(System.currentTimeMillis());
		
		if(		currentDate.compareTo(DateUtils.addMinutes(currentEvent.getStartDate(), -1 * currentEvent.getReminderNumber())) > 0
				&& currentEvent.getReminderNumber() > 0) {
			
			Messages.addGlobalError("Error: the reminder will take place in the past");
			
			
		} else if(currentEvent.getReminderNumber() > 0 && currentEvent.getReminderEmail().isEmpty()) {
			Messages.addGlobalError("Error: please enter an email address to send the reminder to");
			
		} else if (currentEvent.getStartDate().compareTo(currentEvent.getEndDate()) <= 0 ) {
			try {
				eventService.update(currentEvent);
				currentEvent = new CalendarEvent();
				editMode = false;
				editId = null;
				
				Messages.addFlashGlobalInfo("Update was succesful");
				outcome = "list?faces-redirect=true";
			} catch (Exception e) {
				Messages.addGlobalError("Update was not successful");
				logger.fine(e.getMessage());
			}
			
		} else {
			Messages.addGlobalError("Event end date cannot come before event start date");
		}
		
		
		
		return outcome;
	}
	
	public String delete() {
		String nextUrl = null;
		
		try {
			eventService.remove(currentEvent);
			events.remove(currentEvent);
			Faces.redirect("ListEvent.xhtml");
		} catch (Exception e) {
			Messages.addGlobalError("Error: event was not deleted.");
			logger.fine(e.getMessage());
		}
		
		return nextUrl;
		
	}
	
	public String cancel() {
		currentEvent = null;
		editMode = false;
		Faces.redirect("ListEvent.xhtml");
		return "list?faces-redirect=true";
	}
	
	public void emailBoxToggle() {
		if(currentEvent.getReminderNumber() > 0) {
			hideEmailEntry = false;
		} else {
			hideEmailEntry = true;
		}
	}

}
