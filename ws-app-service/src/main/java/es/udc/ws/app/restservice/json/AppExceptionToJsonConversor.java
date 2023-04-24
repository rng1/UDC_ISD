package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.eventservice.exceptions.*;

public class AppExceptionToJsonConversor {

	public static ObjectNode toAlreadyCanceledEventException(AlreadyCanceledEventException ex) {

		ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

		exceptionObject.put("errorType", "AlreadyCanceledEvent");
		exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);

		return exceptionObject;
	}

	public static ObjectNode toOutOfTimeEventException(OutOfTimeEventException ex) {
		ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

		exceptionObject.put("errorType", "OutOfTimeEvent");
		exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
		if (ex.getCelebrationDate() == null) {
			exceptionObject.set("celebrationDate", null);
		} else {
			exceptionObject.put("celebrationDate", ex.getCelebrationDate().toString());
		}
		return exceptionObject;
	}

	public static ObjectNode toReplyOutOfTimeException(ReplyOutOfTimeException ex) {
		ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

		exceptionObject.put("errorType", "ReplyOutOfTimeException");
		if (ex.getCelebrationDate() == null) {
			exceptionObject.set("celebrationDate", null);
		}
			else {
				exceptionObject.put("celebrationDate", ex.getCelebrationDate().toString());
			}
		return exceptionObject;
	}
	public static ObjectNode toCanceledEventException(CanceledEventException ex) {
		ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

		exceptionObject.put("errorType", "CanceledEventException");
		exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);

		return exceptionObject;
	}



	public static ObjectNode toAlreadyRepliedException(AlreadyRepliedException ex) {
		ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

		exceptionObject.put("errorType", "AlreadyRepliedException");
		exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
		exceptionObject.put("userEmail", (ex.getUserEmail() != null) ? ex.getUserEmail() : null);

		return exceptionObject;
	}
}

