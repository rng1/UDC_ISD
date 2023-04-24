package es.udc.ws.app.restservice.dto;

public class RestEventDto{

	private Long eventId;
	private String name;
	private String description;
	private String celebrationDate;
	private String cancelationDate;
	private short duration;
	private short attendees;
	private short absences;

	public RestEventDto(){
	}

	public RestEventDto(Long eventId, String name, String description,
						String celebrationDate, String cancelationDate,
						short duration, short attendees, short absences){
		this.eventId = eventId;
		this.name = name;
		this.description = description;
		this.celebrationDate = celebrationDate;
		this.cancelationDate = cancelationDate;
		this.duration = duration;
		this.attendees = attendees;
		this.absences = absences;
	}

	public Long getEventId(){
		return eventId;
	}
	public void setEventId(Long eventId){
		this.eventId = eventId;
	}

	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}

	public String getDescription(){
		return description;
	}
	public void setDescription(String description){
		this.description = description;
	}

	public String getCelebrationDate(){
		return celebrationDate;
	}
	public void setCelebrationDate(String celebrationDate){
		this.celebrationDate = celebrationDate;
	}
	public String getCancelationDate(){
		return cancelationDate;
	}
	public void setCancelationDate(String cancelationDate){
		this.cancelationDate = cancelationDate;
	}

	public short getDuration(){
		return duration;
	}
	public void setDuration(short duration){
		this.duration = duration;
	}

	public short getAttendees(){
		return attendees;
	}
	public void setAttendees(short attendees){
		this.attendees = attendees;
	}
	public short getAbsences(){
		return absences;
	}
	public void setAbsences(short absences){
		this.absences = absences;
	}

	@Override
	public String toString(){
		return "EventDto [eventId=" + eventId + ", name=" + name
		+ ", description=" + description + "celebrationDate="+celebrationDate
		+ ", cancelationDate=" + cancelationDate
		+ ", duration=" + duration + ", attendees=" + attendees 
		+ ", absences="+ absences + "]";
	}
}