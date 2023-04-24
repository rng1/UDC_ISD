package es.udc.ws.app.restservice.dto;

public class RestReplyDto {
    private Long replyId;
    private Long eventId;
    private String userEmail;
    private Boolean replyValue;

    public RestReplyDto() {
    }

    public RestReplyDto(Long replyId, Long eventId,
                        String userEmail, Boolean replyValue) {
        this.replyId = replyId;
        this.eventId = eventId;
        this.userEmail = userEmail;
        this.replyValue = replyValue;
    }

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Boolean getReplyValue() {
        return replyValue;
    }

    public void setReplyValue(Boolean replyValue) {
        this.replyValue = replyValue;
    }

    @Override
    public String toString() {
        return "ReplyDto [replyId=" + replyId + ", eventId=" + eventId
                + ", userEmail=" + userEmail + ", replyValue=" + replyValue + "]";
    }
}
