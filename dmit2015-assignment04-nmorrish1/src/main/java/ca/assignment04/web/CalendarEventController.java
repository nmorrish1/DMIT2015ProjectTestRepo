package ca.assignment04.web;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import ca.assignment04.entities.CalendarEvent;
import ca.assignment04.service.CalendarEventService;
import lombok.Getter;
import lombok.Setter;

@Named
@ApplicationScoped
public class CalendarEventController {
	
	private static Logger log = Logger.getLogger(CalendarEventController.class.getName());
	
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
	
	@PostConstruct
	void init() {
		try {
			events = eventService.listAllEvents();
		} catch (Exception e) {
			Messages.addGlobalError("Error loading events");
			log.fine(e.getMessage());
		}
	}
	
	public String create() {
		String outcome = null;
		
		try {
			
			if (currentEvent.getStartDate().compareTo(currentEvent.getEndDate()) <= 0 ) {
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
			log.fine(e.getMessage());
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
					log.fine(e.getMessage());	
				}	
			} else {
				Faces.navigate("list?faces-redirect=true");	
			}
		} 
	}
	
	public String update() {
		String outcome = null;
		
		try {
			eventService.update(currentEvent);
			currentEvent = new CalendarEvent();
			editMode = false;
			editId = null;
			
			Messages.addFlashGlobalInfo("Update was succesful");
			outcome = "list?faces-redirect=true";
		} catch (Exception e) {
			Messages.addGlobalError("Update was not successful");
			log.fine(e.getMessage());
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
			log.fine(e.getMessage());
		}
		
		return nextUrl;
		
	}
	
	public String cancel() {
		currentEvent = null;
		editMode = false;
		Faces.redirect("ListEvent.xhtml");
		return "list?faces-redirect=true";
	}

}
