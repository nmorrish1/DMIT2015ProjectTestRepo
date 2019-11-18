package ca.assignment04.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEvent {
	
	@Id
	@Column(name="EventID")
	@GeneratedValue
	private Long eventId;
	
	@Column(name="StartDate")
	private Date startDate;
	
	@Column(name="EndDate")
	private Date endDate;
	
	@Column(name="Location")
	private String location;

	@Column(name="Description")
	private String description;
	
}
