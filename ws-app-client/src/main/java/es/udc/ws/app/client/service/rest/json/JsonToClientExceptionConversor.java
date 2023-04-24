package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyCanceledEventException;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyRepliedException;
import es.udc.ws.app.client.service.exceptions.ClientCanceledEventException;
import es.udc.ws.app.client.service.exceptions.ClientOutOfTimeEventException;
import es.udc.ws.app.client.service.exceptions.ClientReplyOutOfTimeException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;

public class JsonToClientExceptionConversor {

    public static Exception fromBadRequestErrorCode(InputStream ex) throws ParsingException {
        try{
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT){
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InputValidation")){
                    return toInputValidationException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: "+errorType);
                }
            }
        } catch (ParsingException e){
            throw e;
        } catch (Exception e){
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode){
        String message = rootNode.get("message").textValue();
        return new InputValidationException(message);
    }

    public static Exception fromNotFoundErrorCode(InputStream ex) throws ParsingException{
		try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(ex);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				String errorType = rootNode.get("errorType").textValue();
				if (errorType.equals("InstanceNotFound")) {
					return toInstanceNotFoundException(rootNode);
				} else {
					throw new ParsingException("Unrecognized error type: " + errorType);
				}
			}
		} catch (ParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        String instanceId = rootNode.get("instanceId").textValue();
        String instanceType = rootNode.get("instanceType").textValue();
        return new InstanceNotFoundException(instanceId, instanceType);
    }

	public static Exception fromForbiddenErrorCode(InputStream ex) throws ParsingException {
		try {
			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			JsonNode rootNode = objectMapper.readTree(ex);
			if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
				throw new ParsingException("Unrecognized JSON (object expected)");
			} else {
				String errorType = rootNode.get("errorType").textValue();
				if (errorType.equals("AlreadyCanceledEvent")) {
					return toClientAlreadyCanceledEventException(rootNode);
                } else if (errorType.equals("OutOfTimeEvent")) {
                    return toClientOutOfTimeEventException(rootNode);
                } else if (errorType.equals("AlreadyRepliedException")) {
                    return toClientAlreadyRepliedException(rootNode);
                } else if (errorType.equals("ReplyOutOfTimeException")) {
                    return toClientReplyOutOfTimeException(rootNode);
                } else if (errorType.equals("CanceledEventException")) {
                    return toClientCanceledEventException(rootNode);
                }else {
					throw new ParsingException("Unrecognized error type: " + errorType);
				}
			}
		} catch (ParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}


    private static ClientReplyOutOfTimeException toClientReplyOutOfTimeException(JsonNode rootNode){
        String celebrationDate = rootNode.get("celebrationDate").textValue();
        return new ClientReplyOutOfTimeException(LocalDateTime.parse(celebrationDate));
    }
    private static ClientCanceledEventException toClientCanceledEventException(JsonNode rootNode){
        Long eventId = rootNode.get("eventId").longValue();
        return new ClientCanceledEventException(eventId);
    }
    private static ClientAlreadyRepliedException toClientAlreadyRepliedException(JsonNode rootNode){
        Long eventId = rootNode.get("eventId").longValue();
        String userEmail = rootNode.get("userEmail").textValue();
        return new ClientAlreadyRepliedException(eventId, userEmail);
    }
    private static ClientAlreadyCanceledEventException toClientAlreadyCanceledEventException(JsonNode rootNode) {
        Long eventId = rootNode.get("eventId").longValue();
        return new ClientAlreadyCanceledEventException(eventId);
    }
    private static ClientOutOfTimeEventException toClientOutOfTimeEventException(JsonNode rootNode) {
        Long eventId = rootNode.get("eventId").longValue();
        LocalDateTime celebrationDate = LocalDateTime.parse(rootNode.get("celebrationDate").textValue());
        return new ClientOutOfTimeEventException(eventId, celebrationDate);
    }

}