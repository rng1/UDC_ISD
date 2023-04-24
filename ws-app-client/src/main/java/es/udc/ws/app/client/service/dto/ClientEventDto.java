package es.udc.ws.app.client.service.dto;

public class ClientEventDto{

	private Long eventId;
	private String name;
	private String description;
	private String celebrationDate;
	private String cancelationDate;
	private short duration;
	private short attendees;
	private short totalReplies;

	public ClientEventDto(){

	}
	public ClientEventDto(String name, String description,
						  String celebrationDate, short duration) {
		this.name = name;
		this.description = description;
		this.celebrationDate = celebrationDate;
		this.duration = duration;
	}

	public ClientEventDto(Long eventId, String name, String description,
						  String celebrationDate, String cancelationDate,
						  short duration, short attendees, short totalReplies) {
		this.eventId = eventId;
		this.name = name;
		this.description = description;
		this.celebrationDate = celebrationDate;
		this.cancelationDate = cancelationDate;
		this.duration = duration;
		this.attendees = attendees;
		this.totalReplies = totalReplies;
	}

	public Long getEventId(){
		return eventId;
	}
	public void setEventId(Long eventId){
		this.eventId=eventId;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getDescription(){
		return description;
	}
	public void setDescription(String description){
		this.description=description;		
	}
	public String getCelebrationDate(){
		return celebrationDate;
	}
	public void setCelebrationDate(String celebrationDate){
		this.celebrationDate=celebrationDate;		
	}
	public String getCancelationDate(){
		return cancelationDate;
	}
	public void setCancelationDate(String cancelationDate){
		this.cancelationDate=cancelationDate;		
	}
	public short getDuration(){
		return duration;
	}
	public void setDuration(short duration){
		this.duration=duration;		
	}
	public short getAttendees(){
		return attendees;
	}
	public void setAttendees(short attendees){
		this.attendees=attendees;		
	}
	public short getTotalReplies(){
		return totalReplies;
	}
	public void setTotalReplies(short totalReplies){
		this.totalReplies=totalReplies;		
	}

	@Override
	public String toString(){

		return "EventDto [eventId="+eventId+", name="+name+", description="+description+
				", celebrationDate="+celebrationDate+", cancelationDate="+cancelationDate+
				", duration="+duration+", attendees="+attendees+", totalReplies="+totalReplies+"]";
	}


}