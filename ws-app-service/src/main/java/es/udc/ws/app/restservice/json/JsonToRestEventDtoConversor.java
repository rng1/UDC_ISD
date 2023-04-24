package es.udc.ws.app.restservice.json;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;


public class JsonToRestEventDtoConversor {
	public static ObjectNode toObjectNode(RestEventDto event){

		ObjectNode eventObject = JsonNodeFactory.instance.objectNode();

		if (event.getEventId() != null) {
			eventObject.put("eventId", event.getEventId());
		}
		eventObject.put("name", event.getName());
		eventObject.put("description", event.getDescription());
		eventObject.put("celebrationDate", event.getCelebrationDate());
		eventObject.put("cancelationDate", event.getCancelationDate());
		eventObject.put("duration", event.getDuration());
		eventObject.put("attendees", event.getAttendees());
		eventObject.put("total replies", event.getAbsences() + event.getAttendees());

		return eventObject;
	}

	public static ArrayNode toArrayNode(List<RestEventDto> events) {
		ArrayNode eventsNode = JsonNodeFactory.instance.arrayNode();
		for (RestEventDto event: events) {
			ObjectNode eventObject = toObjectNode(event);
			eventsNode.add(eventObject);
		}

		return eventsNode;
	}

	public static RestEventDto toRestEventDto(InputStream jsonEvent) throws ParsingException {
		try {
			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(jsonEvent);

			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				ObjectNode eventObject = (ObjectNode) rootNode;

				JsonNode eventIdNode = eventObject.get("eventId");
				Long eventId = (eventIdNode != null) ? eventIdNode.longValue() : null;

				String name = eventObject.get("name").textValue().trim();
				String description = eventObject.get("description").textValue().trim();
				String celebrationDate = eventObject.get("celebrationDate").textValue().trim();
				String cancelationDate;
				if (eventObject.get("cancelationDate").isNull()){
					cancelationDate = null;
				} else {
					cancelationDate = eventObject.get("cancelationDate").textValue().trim();
				}
				short duration = eventObject.get("duration").shortValue();
				short attendees;
				if (eventObject.get("attendees") != null){
					attendees = eventObject.get("attendees").shortValue();
				} else {
					attendees = 0;
				}
				short absences;
				if (eventObject.get("absences") != null){
					absences = eventObject.get("absences").shortValue();
				} else {
					absences = 0;
				}

				return new RestEventDto(eventId, name, description, celebrationDate, cancelationDate, duration, attendees, absences);
			}
		} catch (ParsingException ex){
			throw ex;
		} catch (Exception e){
			throw new ParsingException(e);
		}
	}
}