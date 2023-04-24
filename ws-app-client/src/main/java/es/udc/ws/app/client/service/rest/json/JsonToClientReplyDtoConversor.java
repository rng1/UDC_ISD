package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientReplyDtoConversor {

    public static ClientReplyDto toClientReplyDto(InputStream jsonReply) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonReply);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode replyObject = (ObjectNode) rootNode;

                JsonNode replyIdNode = replyObject.get("replyId");
                Long replyId = (replyIdNode != null) ? replyIdNode.longValue() : null;

                Long eventId = replyObject.get("eventId").longValue();
                String userEmail = replyObject.get("userEmail").textValue().trim();
                Boolean replyValue = replyObject.get("replyValue").booleanValue();

                return new ClientReplyDto(replyId, eventId, userEmail, replyValue);

            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static ClientReplyDto toClientReplyDto(JsonNode replyNode) throws ParsingException{
        if (replyNode.getNodeType() != JsonNodeType.OBJECT){
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode replyObject = (ObjectNode) replyNode;

            JsonNode replyIdNode = replyNode.get("replyId");
            Long replyId = (replyIdNode != null) ? replyIdNode.longValue() : null;

            Long eventId = replyObject.get("eventId").longValue();
            String userEmail = replyObject.get("userEmail").textValue().trim();
            Boolean replyValue = replyObject.get("replyValue").booleanValue();

            return new ClientReplyDto(replyId, eventId, userEmail, replyValue);
        }
    }

    public static List<ClientReplyDto> toClientReplyDtos(InputStream jsonReplies) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonReplies);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode replies = (ArrayNode) rootNode;
                List<ClientReplyDto> replyDtos = new ArrayList<>(replies.size());
                for (JsonNode replyNode : replies) {
                    replyDtos.add(toClientReplyDto(replyNode));
                }
                return replyDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }


}
