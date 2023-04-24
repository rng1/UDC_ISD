package es.udc.ws.app.restservice.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.*;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.app.restservice.dto.EventToRestEventDtoConversor;
import es.udc.ws.app.restservice.json.JsonToRestEventDtoConversor;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.ServletUtils;
import es.udc.ws.util.servlet.RestHttpServletTemplate;

@SuppressWarnings("serial")
public class EventsServlet extends RestHttpServletTemplate{

	@Override
	protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException, InstanceNotFoundException{
        String url = ServletUtils.normalizePath(req.getRequestURL().toString());
        String[] segments = url.split("/");


		if (segments[segments.length-1].equals("cancel")) {

			Long eventId = ServletUtils.getMandatoryParameterAsLong(req, "eventId");
			
			try{
				EventServiceFactory.getService().cancelEvent(eventId);
			} catch (OutOfTimeEventException ex){
				ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toOutOfTimeEventException(ex), null);
				return;
			} catch (AlreadyCanceledEventException ex){
				ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toAlreadyCanceledEventException(ex), null);
				return;
			}
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT, null, null);
		} else {
			RestEventDto eventDto = JsonToRestEventDtoConversor.toRestEventDto(req.getInputStream());
			Event event = EventToRestEventDtoConversor.toEvent(eventDto);

			event = EventServiceFactory.getService().addEvent(event);

			eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
			String eventURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + event.getEventId();
			Map<String, String> headers = new HashMap<>(1);
			headers.put("Location", eventURL);
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED, JsonToRestEventDtoConversor.toObjectNode(eventDto), headers);
		}
	}

	@Override
	protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
			InputValidationException, InstanceNotFoundException {

		if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
			ServletUtils.checkEmptyPath(req);
			String keywords = req.getParameter("keywords");
			String endDate = req.getParameter("endDate") + "T00:00";

			List<Event> events = EventServiceFactory.getService().detailedFind(LocalDateTime.now(),
					LocalDateTime.parse(endDate), keywords);

			List<RestEventDto> eventDtos = EventToRestEventDtoConversor.toRestEventDtos(events);
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
					JsonToRestEventDtoConversor.toArrayNode(eventDtos), null);
		} else {
			Long eventId = ServletUtils.getIdFromPath(req, "event");

			Event event = EventServiceFactory.getService().find(eventId);
			RestEventDto eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
					JsonToRestEventDtoConversor.toObjectNode(eventDto), null);
		}
	}

/*	private void getFindEvents (HttpServletRequest req, HttpServletResponse resp)
		throws InputValidationException, IOException {

		ServletUtils.checkEmptyPath(req);
		String keywords = req.getParameter("keywords");
		String endDate = req.getParameter("endDate") + "T00:00";

		List<Event> events = EventServiceFactory.getService().detailedFind(LocalDateTime.now(),
				LocalDateTime.parse(endDate), keywords);

		List<RestEventDto> eventDtos = EventToRestEventDtoConversor.toRestEventDtos(events);
		ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
				JsonToRestEventDtoConversor.toArrayNode(eventDtos), null);
	}

	private void getFindEvent (HttpServletRequest req, HttpServletResponse resp)
			throws InputValidationException, IOException, InstanceNotFoundException {
		Long eventId = ServletUtils.getIdFromPath(req, "event");

		Event event = EventServiceFactory.getService().find(eventId);
		RestEventDto eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
		ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
				JsonToRestEventDtoConversor.toObjectNode(eventDto), null);
	}*/
}