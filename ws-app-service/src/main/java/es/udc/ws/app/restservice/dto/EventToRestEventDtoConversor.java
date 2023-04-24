package es.udc.ws.app.restservice.dto;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import es.udc.ws.app.model.event.Event;

public class EventToRestEventDtoConversor{
	public static List<RestEventDto> toRestEventDtos(List<Event> events){
		List<RestEventDto> eventDtos = new ArrayList<>(events.size());
		for (Event event : events){
			eventDtos.add(toRestEventDto(event));
		}
		return eventDtos;
	}

	public static RestEventDto toRestEventDto(Event event){
		String cancelationDate;
		if (event.getCancelationDate() == null){
			cancelationDate = null;
		} else {
			cancelationDate = event.getCancelationDate().toString();
		}
		return new RestEventDto(event.getEventId(), event.getName(), event.getDescription(),
								event.getCelebrationDate().toString(), cancelationDate,
								event.getDuration(), event.getAttendees(), event.getAbsences());
	}

	public static Event toEvent(RestEventDto event){
		LocalDateTime cancelationDate;
		if (event.getCancelationDate() == null){
			cancelationDate = null;
		} else {
			cancelationDate = LocalDateTime.parse(event.getCancelationDate());
		}
		return new Event(event.getEventId(), event.getName(), event.getDescription(),
						LocalDateTime.parse(event.getCelebrationDate()), cancelationDate,
						event.getDuration(), event.getAttendees(), event.getAbsences());
	}
}