package es.udc.ws.app.model.eventservice.exceptions;

import java.time.LocalDateTime;

public class ReplyOutOfTimeException extends Exception{

    private LocalDateTime celebrationDate;
    public ReplyOutOfTimeException(LocalDateTime celebrationDate) {
        super("Reply can't be registered: Less than 24 hours to the event celebration ["+celebrationDate+"]");
        this.celebrationDate = celebrationDate;
    }

    public LocalDateTime getCelebrationDate() { return celebrationDate; }

    public void setCelebrationDate(LocalDateTime celebrationDate) { this.celebrationDate = celebrationDate; }
}



