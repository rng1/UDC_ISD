package es.udc.ws.app.thriftservice;

import es.udc.ws.app.thrift.ThriftEventDto;
import es.udc.ws.app.model.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class EventToThriftEventDtoConversor {

	public static Event toEvent(ThriftEventDto event){
		LocalDateTime cancelationDate;
		if (event.getCancelationDate() == null){
			cancelationDate = null;
		} else {
			cancelationDate = LocalDateTime.parse(event.getCancelationDate());
		}
		return new Event(event.getName(), event.getDescription(), LocalDateTime.parse(event.getCelebrationDate()), event.getDuration());
	}

	public static List<ThriftEventDto> toThriftEventDtos(List<Event> events){
		List<ThriftEventDto> dtos = new ArrayList<>(events.size());

		for (Event event: events) {
			dtos.add(toThriftEventDto(event));
		}
		return dtos;
	}

	public static ThriftEventDto toThriftEventDto(Event event){
		String cancelationDate;
		if (event.getCancelationDate() == null){
			cancelationDate = null;
		} else {
			cancelationDate = event.getCancelationDate().toString();
		}
		int totalReplies = (int) event.getAttendees() + (int) event.getAbsences();

		return new ThriftEventDto(event.getEventId(), event.getName(),
						event.getDescription(), event.getCelebrationDate().toString(),
						cancelationDate, event.getDuration(), 
						event.getAttendees(), (short) totalReplies);
	}
}