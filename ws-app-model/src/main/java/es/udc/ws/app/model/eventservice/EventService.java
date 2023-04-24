package es.udc.ws.app.model.eventservice;

import es.udc.ws.app.model.event.Event;

import es.udc.ws.app.model.eventservice.exceptions.*;

import es.udc.ws.app.model.reply.Reply;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

	public Event addEvent(Event event) throws 
		InputValidationException;

	Event find(Long eventId) throws InstanceNotFoundException;

	List<Event> detailedFind(LocalDateTime start,
							 LocalDateTime end, String keywords);

	public Reply makeReply (Long eventId, String email, Boolean replyValue) throws
		InstanceNotFoundException, InputValidationException, AlreadyRepliedException, ReplyOutOfTimeException, CanceledEventException;


	public void cancelEvent(Long eventId) throws 
		AlreadyCanceledEventException, OutOfTimeEventException, InstanceNotFoundException;


	public List<Reply> findUserReplies (String user_email, Boolean only_yes) throws
		InputValidationException;

}