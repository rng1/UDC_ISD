package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.thrift.ThriftEventDto;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class ClientEventDtoToThriftEventDtoConversor {
	public static ThriftEventDto toThriftEventDto(ClientEventDto clientEventDto){
		Long eventId = clientEventDto.getEventId();
		return new ThriftEventDto(eventId == null ? -1 : eventId.longValue(),
						clientEventDto.getName(), clientEventDto.getDescription(),
						clientEventDto.getCelebrationDate(), clientEventDto.getCancelationDate(),
						clientEventDto.getDuration(), clientEventDto.getAttendees(), clientEventDto.getTotalReplies());
	}

	public static List<ClientEventDto> toClientEventDtos(List<ThriftEventDto> events){
		List<ClientEventDto> clientEventDtos = new ArrayList<>(events.size());
		for (ThriftEventDto event: events){
			clientEventDtos.add(toClientEventDto(event));
		}
		return clientEventDtos;
	}

	public static ClientEventDto toClientEventDto(ThriftEventDto event){
		String cancelationDate = event.getCancelationDate();

		return new ClientEventDto(event.getEventId(), event.getName(),
			event.getDescription(), event.getCelebrationDate(),
			cancelationDate, event.getDuration(), event.getAttendees(), event.getTotalReplies());
	}
}