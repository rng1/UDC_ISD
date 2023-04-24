package es.udc.ws.app.client.service.exceptions;


public class ClientCanceledEventException extends Exception {
    private Long eventId;

    public ClientCanceledEventException(Long eventId) {
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