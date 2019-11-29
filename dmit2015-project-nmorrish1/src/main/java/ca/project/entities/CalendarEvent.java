package ca.project.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEvent implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long eventId;
	
	@Column(name="EventName")
	@NotBlank(message = "Event name is required")
	private String eventName;
	
	@Column(name="StartDate")
	@Future(message = "Event start must occur in the future")
	private Date startDate;
	
	@Column(name="EndDate")
	@Future(message = "Event end must occur in the future")
	private Date endDate;
	
	@Column(name="Location")
	private String location;

	@Column(name="Description")
	private String description;
	
	@Column(name="ReminderNumber")
	//@PositiveOrZero(message = "Minutes to reminder must be greater than or equal to zero")
	private Integer reminderNumber;
	
	@Column(name="ReminderEmail")
	//@Pattern(regexp="^.+@.+\\.[a-zA-Z]{2,4}$", message = "Please enter a valid email address")
	private String reminderEmail;
	
	@Column(nullable = false)
	@NotBlank(message = "Username is required")
	private String username;
	
	
	
}
