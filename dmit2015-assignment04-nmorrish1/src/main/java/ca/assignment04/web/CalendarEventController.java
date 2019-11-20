package ca.assignment04.web;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
	private CalendarEvent currentEvent = new CalendarEvent();
	
	@Getter @Setter
	private Integer reminderMinutes = 0;
	
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
			eventService.add(currentEvent);
			currentEvent = new CalendarEvent();
			Messages.addFlashGlobalInfo("Event successfully added to calendar");
			outcome = "list?faces-redirect=true";
			
			events = eventService.listAllEvents();
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
						Messages.addFlashGlobalError("{0} is not a valid id value", editId);
						Faces.navigate("list?faces-redirect=true");						
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

}
