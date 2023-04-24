package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.eventservice.exceptions.*;
import es.udc.ws.app.model.reply.Reply;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public class ThriftEventServiceImpl implements ThriftEventService.Iface {

	@Override
	public ThriftEventDto addEvent(ThriftEventDto eventDto) throws ThriftInputValidationException{
		Event event = EventToThriftEventDtoConversor.toEvent(eventDto);

		try{
			Event addedEvent = EventServiceFactory.getService().addEvent(event);
			return EventToThriftEventDtoConversor.toThriftEventDto(addedEvent);
		} catch (InputValidationException e){
			throw new ThriftInputValidationException(e.getMessage());
		}
	}

	@Override
	public List<ThriftEventDto> findEvents(String endDate, String keywords) {
		List<Event> events = EventServiceFactory.getService().detailedFind(LocalDateTime.now(), LocalDateTime.parse(endDate), keywords);
		return EventToThriftEventDtoConversor.toThriftEventDtos(events);
	}

	@Override
	public ThriftEventDto findEvent(long eventId) throws ThriftInstanceNotFoundException {

		try {
			Event event = EventServiceFactory.getService().find(eventId);
			return EventToThriftEventDtoConversor.toThriftEventDto(event);
		} catch (InstanceNotFoundException e){
			throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
					e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
		}

	}

	@Override
	public ThriftReplyDto makeReply(long eventId, String email, boolean replyValue) throws
			ThriftInstanceNotFoundException, ThriftInputValidationException, ThriftAlreadyRepliedException, ThriftReplyOutOfTimeException, ThriftCanceledEventException{

		try {
			Reply reply = EventServiceFactory.getService().makeReply(eventId,email,replyValue);
			return ReplyToThriftReplyDtoConversor.toThriftReplyDto(reply);

		} catch (InstanceNotFoundException e) {
			throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
					e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
		} catch (InputValidationException e) {
			throw new ThriftInputValidationException(e.getMessage());
		} catch (AlreadyRepliedException e) {
			throw new ThriftAlreadyRepliedException(e.getEventId(),e.getUserEmail());
		} catch (ReplyOutOfTimeException e) {
			throw new ThriftReplyOutOfTimeException(e.getCelebrationDate().toString());
		} catch (CanceledEventException e) {
			throw new ThriftCanceledEventException(e.getEventId());
		}

	}

	@Override
	public void cancelEvent(long eventId) throws ThriftInstanceNotFoundException,
			ThriftAlreadyCanceledEventException, ThriftOutOfTimeEventException {
		try {
			EventServiceFactory.getService().cancelEvent(eventId);

		} catch (OutOfTimeEventException e) {
			throw new ThriftOutOfTimeEventException(e.getEventId(), e.getCelebrationDate().toString());
		} catch (InstanceNotFoundException e) {
			throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
					e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
		} catch (AlreadyCanceledEventException e) {
			throw new ThriftAlreadyCanceledEventException(e.getEventId());
		}
	}

	@Override
	public List<ThriftReplyDto> findUserReplies(String user_email, boolean only_yes) throws ThriftInputValidationException {

		try {
			List<Reply> answers = EventServiceFactory.getService().findUserReplies(user_email, only_yes);
			return ReplyToThriftReplyDtoConversor.toThriftReplyListDto(answers);
		} catch (InputValidationException e) {
			throw new ThriftInputValidationException(e.getMessage());
		}
	}
}
