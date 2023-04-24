package es.udc.ws.app.client.service.rest.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToClientEventDtoConversor{
	public static ObjectNode toObjectNode(ClientEventDto event) throws IOException{

		ObjectNode eventObject = JsonNodeFactory.instance.objectNode();

		if (event.getEventId() != null){
			eventObject.put("eventId", event.getEventId());
		}
		eventObject.
			put("name", event.getName()).
			put("description", event.getDescription()). 
			put("celebrationDate", event.getCelebrationDate()). 
			put("cancelationDate", event.getCancelationDate()). 
			put("duration", event.getDuration()). 
			put("attendees", event.getAttendees()). 
			put("totalReplies", event.getTotalReplies());
		return eventObject;
	}

	public static ClientEventDto toClientEventDto(InputStream jsonEvent) throws ParsingException{
		try {
			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(jsonEvent);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT){
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				return toClientEventDto(rootNode);
			}
		} catch (ParsingException ex){
			throw ex;
		} catch (Exception e){
			throw new ParsingException(e);
		}
	}

	public static ClientEventDto toClientEventDto(JsonNode eventNode) throws ParsingException{
		if (eventNode.getNodeType() != JsonNodeType.OBJECT){
			throw new ParsingException("Unrecognized JSON (object expected)");
		} else {
			ObjectNode eventObject = (ObjectNode) eventNode;

			JsonNode eventIdNode = eventNode.get("eventId");
			Long eventId = (eventIdNode != null) ? eventIdNode.longValue() : null;

			String name = eventObject.get("name").textValue().trim();
			String description= eventObject.get("description").textValue().trim();
			String celebrationDate= eventObject.get("celebrationDate").textValue().trim();
			String cancelationDate;
			if (eventObject.get("cancelationDate").isNull() ){
				cancelationDate = null;
			} else {
				cancelationDate = eventObject.get("cancelationDate").textValue().trim();
			}
			short duration = eventObject.get("duration").shortValue();
			short attendees;
			if (eventObject.get("attendees") == null){
				attendees = 0;
			} else {
				attendees = eventObject.get("attendees").shortValue();
			}
			short totalReplies;
			if (eventObject.get("totalReplies") == null){
				totalReplies = 0;
			} else {
				totalReplies = eventObject.get("totalReplies").shortValue();
			}

			return new ClientEventDto(eventId, name, description, celebrationDate, cancelationDate, duration, attendees, totalReplies);
		}
	}

	public static List<ClientEventDto> toClientEventDtos(InputStream jsonEvents) throws ParsingException {
		try {

			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(jsonEvents);
			if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
				throw new ParsingException("Unrecognized JSON (array expected)");
			} else {
				ArrayNode events = (ArrayNode) rootNode;
				List<ClientEventDto> eventDtos = new ArrayList<>(events.size());
				for (JsonNode eventNode : events) {
					eventDtos.add(toClientEventDto(eventNode));
				}

				return eventDtos;
			}
		} catch (ParsingException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}
}