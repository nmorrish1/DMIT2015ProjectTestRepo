package ca.assignment04.web;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.omnifaces.util.Messages;

import ca.assignment04.entities.CalendarEvent;
import ca.assignment04.service.CalendarEventService;
import lombok.Getter;
import lombok.Setter;

@ApplicationScoped
public class CalendarEventController {
	
	private static Logger log = Logger.getLogger(CalendarEventController.class.getName());
	
	@Inject
	private CalendarEventService eventService;
	
	private List<CalendarEvent> events;
	
	@Getter @Setter
	private CalendarEvent currentEvent;
	
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
		} catch (Exception e) {
			Messages.addGlobalError("Error: could not add event");
			log.fine(e.getMessage());
		}
		
		return outcome;
	}
}