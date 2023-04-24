package es.udc.ws.app.model.event;

import java.time.LocalDateTime;

public class Event {
	private Long eventId;					//id del evento
	private String name;					//nombre del evento
	private String description;				//descripcion del evento
	private LocalDateTime celebrationDate;	//fecha en que se celebra el evento
	private LocalDateTime creationDate;		//fecha en la que se crea el evento
	private LocalDateTime cancelationDate;	//si no es null es que esta cancelado
	private short duration;					//duracion del evento
	private short attendees;
	private short absences;

	public Event(String name, String description, LocalDateTime celebrationDate, short duration){
		this.name = name;
		this.description = description;
		this.celebrationDate = celebrationDate;
		this.duration = duration;
	}

	public Event(Long eventId, String name, String description,
				 LocalDateTime celebrationDate,	LocalDateTime cancelationDate,
				 short duration, short attendees, short absences) {
		this(name,description,celebrationDate,duration);
		this.eventId = eventId;
		this.cancelationDate = cancelationDate;
		this.attendees = attendees;
		this.absences = absences;
	}
	public Event(Long eventId, String name, String description, LocalDateTime celebrationDate,
				 LocalDateTime cancelationDate, LocalDateTime creationDate,
				 short duration, short attendees, short absences) {
		this(eventId,name,description,celebrationDate,cancelationDate,duration,attendees,absences);
		this.creationDate = creationDate;
	}

	public long getEventId(){
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

	public LocalDateTime getCelebrationDate(){
		return celebrationDate;
	}

	public void setCelebrationDate(LocalDateTime celebrationDate){
		this.celebrationDate = celebrationDate;
	}

	public short getDuration(){
		return duration;
	}

	public void setDuration(short duration){
		this.duration = duration;
	}

	public LocalDateTime getCreationDate(){
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate){
		this.creationDate = creationDate;
	}

	public LocalDateTime getCancelationDate(){
		return cancelationDate;
	}

	public void setCancelationDate(LocalDateTime cancelationDate){
		this.cancelationDate = cancelationDate;
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Event other = (Event) obj;

		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;

		if (celebrationDate == null) {
			if (other.celebrationDate != null)
				return false;
		} else if (!celebrationDate.equals(other.celebrationDate))
			return false;

		if (duration == 0) {
			if (other.duration != 0)
				return false;
		} else if (duration != other.duration)
			return false;
		if (attendees == 0) {
			if (other.attendees != 0)
				return false;
		} else if (attendees != other.attendees)
			return false;
		if (absences == 0) {
			if (other.absences != 0)
				return false;
		} else if (absences != other.absences)
			return false;

		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;

		if (cancelationDate == null) {
			if (other.cancelationDate != null)
				return false;
		} else if (cancelationDate != other.cancelationDate)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 59;
		int result = 1;

		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((celebrationDate == null) ? 0 : celebrationDate.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((cancelationDate == null) ? 0 : cancelationDate.hashCode());
		result = prime * result + duration;
		result = prime * result + attendees;
		result = prime * result + absences;

		return result;
	}
}
