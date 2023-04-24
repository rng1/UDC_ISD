package es.udc.ws.app.model.reply;

import java.time.LocalDateTime;


public class Reply {
    private Long replyId;                     // Id de la respuesta
    private Long eventId;                     // Evento al que el usuario responde si acudirá o no
    private String userEmail;                 // Email del usuario que efectuó la respuesta
    private LocalDateTime replyDate;          // Fecha en la que se efectuó la respuesta
    private Boolean replyValue;               // True = Si que acudirá; False = No acudirá


    public Reply (Long eventId, String userEmail, Boolean replyValue){
        this.eventId = eventId;
        this.userEmail = userEmail;
        this.replyValue = replyValue;
    }


    public Reply (Long eventId, String userEmail, LocalDateTime replyDate, Boolean replyValue){
        this.eventId = eventId;
        this.userEmail = userEmail;
        this.replyDate = (replyDate != null) ? replyDate.withNano(0) : null;
        this.replyValue = replyValue;
    }

    public Reply (Long replyId, Long eventId, String userEmail, LocalDateTime replyDate, Boolean replyValue){
        this(eventId, userEmail, replyDate, replyValue);
        this.replyId = replyId;
    }

    public Long getReplyId() { return replyId; }

    public void setReplyId(Long replyId) { this.replyId = replyId; }

    public Long getEventId() { return eventId; }

    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getUserEmail() { return userEmail; }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public LocalDateTime getReplyDate() { return replyDate; }

    public void setReplyDate(LocalDateTime replyDate) { this.replyDate = replyDate.withNano(0); }

    public Boolean getReplyValue() { return replyValue; }

    public void setReplyValue(Boolean replyValue) { this.replyValue = replyValue; }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Reply other = (Reply) obj;

        if (replyId == null) {
            if (other.replyId != null)
                return false;
        } else if (!replyId.equals(other.replyId))
            return false;

        if (eventId == null) {
            if (other.eventId != null)
                return false;
        } else if (!eventId.equals(other.eventId))
            return false;

        if (userEmail == null) {
            if (other.userEmail != null)
                return false;
        } else if (!userEmail.equals(other.userEmail))
            return false;

        if (replyDate == null) {
            if (other.replyDate != null)
                return false;
        } else if (!replyDate.equals(other.replyDate))
            return false;

        if (replyValue == null) {
            if (other.replyValue != null)
                return false;
        } else if (!replyValue.equals(other.replyValue))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 59;
        int result = 1;

        result = prime * result + ((replyId == null) ? 0 : replyId.hashCode());
        result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
        result = prime * result + ((userEmail == null) ? 0 : userEmail.hashCode());
        result = prime * result + ((replyDate == null) ? 0 : replyDate.hashCode());
        result = prime * result + ((replyValue == null) ? 0 : replyValue.hashCode());

        return result;
    }

    }