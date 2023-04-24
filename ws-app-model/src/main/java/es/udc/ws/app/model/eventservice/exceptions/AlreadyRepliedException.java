package es.udc.ws.app.model.eventservice.exceptions;

public class AlreadyRepliedException extends Exception{
    private Long eventId;
    private String userEmail;

    public AlreadyRepliedException(Long eventId, String userEmail) {
        super("Event with id = "+ eventId +" is already replied by user "+ userEmail +".");
        this.eventId = eventId;
        this.userEmail = userEmail;
    }

    public Long getEventId(){
        return eventId;
    }

    public String getUserEmail(){
        return userEmail;
    }

    public void setEventId(Long eventId){
        this.eventId = eventId;
    }

    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }

}





