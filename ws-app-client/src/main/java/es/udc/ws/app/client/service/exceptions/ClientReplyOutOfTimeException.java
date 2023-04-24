package es.udc.ws.app.client.service.exceptions;

import java.time.LocalDateTime;

public class ClientReplyOutOfTimeException extends Exception{

    private LocalDateTime celebrationDate;
    public ClientReplyOutOfTimeException(LocalDateTime celebrationDate) {
        super("Reply can't be registered: Less than 24 hours to the event celebration ["+celebrationDate+"]");
        this.celebrationDate = celebrationDate;
    }

    public LocalDateTime getCelebrationDate() { return celebrationDate; }

    public void setCelebrationDate(LocalDateTime celebrationDate) { this.celebrationDate = celebrationDate; }
}
