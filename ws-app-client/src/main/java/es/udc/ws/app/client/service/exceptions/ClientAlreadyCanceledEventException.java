package es.udc.ws.app.client.service.exceptions;

public class ClientAlreadyCanceledEventException extends Exception{
    private Long eventId;

    public ClientAlreadyCanceledEventException(Long eventId){
        super("Event with id '" +eventId+"' is already canceled");
        this.eventId = eventId;
    }

    public Long getEventId(){
        return eventId;
    }

    public void setEventId(Long eventId){
        this.eventId = eventId;
    }
}