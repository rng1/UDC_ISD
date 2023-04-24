package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestReplyDto;

import java.util.List;

public class JsonToRestReplyDtoConversor {

    public static ObjectNode toObjectNode(RestReplyDto reply) {

        ObjectNode replyNode = JsonNodeFactory.instance.objectNode();

        if (reply.getReplyId() != null) {
            replyNode.put("replyId", reply.getReplyId());
        }
        replyNode.put("eventId", reply.getEventId()).
                put("userEmail", reply.getUserEmail()).
                put("replyValue", reply.getReplyValue());

        return replyNode;
    }

    public static ArrayNode toArrayNode(List<RestReplyDto> replies) {

        ArrayNode eventsNode = JsonNodeFactory.instance.arrayNode();
        for (RestReplyDto replyDto : replies) {
            ObjectNode eventObject = toObjectNode(replyDto);
            eventsNode.add(eventObject);
        }

        return eventsNode;
    }

}
