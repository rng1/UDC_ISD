package es.udc.ws.app.model.eventservice.exceptions;

public class AlreadyCanceledEventException extends Exception {
	private Long eventId;

	public AlreadyCanceledEventException(Long eventId) {
		super("Event with id = "+ eventId +" is already canceled.");
		this.eventId = eventId;
	}

	public Long getEventId(){
		return eventId;
	}

	public void setEventId(Long eventId){
		this.eventId = eventId;
	}

}