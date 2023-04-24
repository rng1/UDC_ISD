package es.udc.ws.app.client.service.exceptions;

import java.time.LocalDateTime;

public class ClientOutOfTimeEventException extends Exception{

	private Long eventId;
	private LocalDateTime celebrationDate;

	public ClientOutOfTimeEventException(Long eventId, LocalDateTime celebrationDate){
        super("Event with id '"+eventId+"' cannot be canceled (already celebrated)\n" +
        	  "  Celebration date: '" + celebrationDate+ "'");
        this.eventId=eventId;
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