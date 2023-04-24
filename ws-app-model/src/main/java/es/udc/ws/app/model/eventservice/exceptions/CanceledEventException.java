package es.udc.ws.app.model.eventservice.exceptions;

public class CanceledEventException extends Exception {
    private Long eventId;

    public CanceledEventException(Long eventId) {
        super("Event with id = "+ eventId +" is canceled.");
        this.eventId = eventId;
    }

    public Long getEventId(){
        return eventId;
    }

    public void setEventId(Long eventId){
        this.eventId = eventId;
    }

}
