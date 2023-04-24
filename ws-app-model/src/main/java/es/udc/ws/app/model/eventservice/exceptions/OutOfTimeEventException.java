package es.udc.ws.app.model.eventservice.exceptions;

import java.time.LocalDateTime;

@SuppressWarnings("serial")
public class OutOfTimeEventException extends Exception {

	private Long eventId;
	private LocalDateTime celebrationDate;

	public OutOfTimeEventException(Long eventId, LocalDateTime celebrationDate){
		super("Can't cancel an event ("+eventId+") already celebrated ("+celebrationDate+")");
		this.eventId = eventId;
		this.celebrationDate = celebrationDate;
	}

	public Long getEventId(){
		return eventId;
	}
	public LocalDateTime getCelebrationDate(){
		return celebrationDate;
	}
	public void setEventId(Long eventId){
		this.eventId = eventId;
	}
	public void setCelebrationDate(LocalDateTime celebrationDate){
		this.celebrationDate = celebrationDate;
	}
}
