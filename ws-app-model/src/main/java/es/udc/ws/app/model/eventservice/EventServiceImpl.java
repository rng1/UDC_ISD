package es.udc.ws.app.model.eventservice;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyRepliedException;
import es.udc.ws.app.model.eventservice.exceptions.CanceledEventException;
import es.udc.ws.app.model.eventservice.exceptions.ReplyOutOfTimeException;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyCanceledEventException;
import es.udc.ws.app.model.eventservice.exceptions.OutOfTimeEventException;
import es.udc.ws.app.model.reply.Reply;

import es.udc.ws.app.model.reply.SqlReplyDao;
import es.udc.ws.app.model.reply.SqlReplyDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;

public class EventServiceImpl implements EventService {
	private final DataSource dataSource;
	private SqlEventDao eventDao = null;
	private SqlReplyDao replyDao = null;

	public EventServiceImpl() {
		dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
		eventDao = SqlEventDaoFactory.getDao();
		replyDao = SqlReplyDaoFactory.getDao();
	}

	private void validateEvent(Event event) throws InputValidationException {
		PropertyValidator.validateMandatoryString("name",event.getName());
		PropertyValidator.validateMandatoryString("description",event.getDescription());
		PropertyValidator.validateNotNegativeLong("duration",event.getDuration());
	}

	private void validateReply(Reply reply) throws InputValidationException {
		if (reply.getEventId() == null || reply.getReplyValue() == null) {
			throw new InputValidationException("EventId y/o ReplyValue no puede ser nulo");
		}
		PropertyValidator.validateNotNegativeLong("eventId",reply.getEventId());
		PropertyValidator.validateMandatoryString("userEmail",reply.getUserEmail());
	}

	@Override
	public Event addEvent(Event event) throws InputValidationException {
		validateEvent(event);
		event.setCreationDate(LocalDateTime.now().withNano(0));
		event.setCancelationDate(null);
		event.setAbsences((short) 0);
		event.setAttendees((short) 0);

		if (!event.getCelebrationDate().isAfter(LocalDateTime.now().withNano(0))){
			throw new InputValidationException("Celebration date cannot be prior to current time");
		}

		try (Connection connection = dataSource.getConnection()) {

			try {

				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				connection.setAutoCommit(false);

				Event createdEvent = eventDao.create(connection, event);

				connection.commit();

				return createdEvent;

			} catch (SQLException e) {
				connection.rollback();
				throw new RuntimeException(e);
			} catch (RuntimeException | Error e) {
				connection.rollback();
				throw e;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Event find(Long eventId) throws InstanceNotFoundException {

		try (Connection connection = dataSource.getConnection()) {
			return eventDao.find(connection, eventId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public List<Event> detailedFind(LocalDateTime startDate,
									LocalDateTime endDate, String keywords) {
		try (Connection connection = dataSource.getConnection()) {
			return eventDao.detailedFind(connection, startDate, endDate, keywords);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Reply makeReply(Long eventId, String email, Boolean replyValue) throws InstanceNotFoundException, InputValidationException, CanceledEventException, AlreadyRepliedException, ReplyOutOfTimeException{
		Reply reply = new Reply(eventId, email, replyValue);
		reply.setReplyDate(LocalDateTime.now());
		validateReply(reply);
		Event event = null;

		try(Connection connection = dataSource.getConnection()){
			try {
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				connection.setAutoCommit(false);
				//get the event
				event = eventDao.find(connection, reply.getEventId());

				//comprobations
				if (event.getCancelationDate() != null){
					connection.commit();
					throw new CanceledEventException(event.getEventId());
				}

				if (replyDao.existsReply(connection, reply.getEventId(), reply.getUserEmail())){
					connection.commit();
					throw new AlreadyRepliedException(reply.getEventId(), reply.getUserEmail());
				}

				if (event.getCelebrationDate().compareTo(LocalDateTime.now().plusHours(24)) < 0) {
					connection.commit();
					throw new ReplyOutOfTimeException(reply.getReplyDate());
				}

				//reply
				Reply createdReply = replyDao.create(connection,reply);
				connection.commit();

				//update event attendees/absences
				if (reply.getReplyValue()){
					event.setAttendees((short) ((int)event.getAttendees() + 1));
				} else {
					event.setAbsences((short) ((int)event.getAbsences() + 1));
				}
				eventDao.update(connection,event);

				//return reply
				return createdReply;

			} catch (InstanceNotFoundException e){
				connection.commit();
				throw e;
			} catch (SQLException e){
				connection.rollback();
				throw new RuntimeException(e);
			} catch (RuntimeException | Error e) {
				connection.rollback();
				throw e;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void cancelEvent(Long eventId) throws AlreadyCanceledEventException, OutOfTimeEventException, InstanceNotFoundException {
		try (Connection connection = dataSource.getConnection()) {
			try{
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				connection.setAutoCommit(false);

				Event retrievedEvent = eventDao.find(connection, eventId);

				if (retrievedEvent.getCancelationDate() != null){
					throw new AlreadyCanceledEventException(retrievedEvent.getEventId());
				}
				if (!retrievedEvent.getCelebrationDate().isAfter(LocalDateTime.now())){
					throw new OutOfTimeEventException(retrievedEvent.getEventId(), retrievedEvent.getCelebrationDate());
				}

				retrievedEvent.setCancelationDate(LocalDateTime.now());
				eventDao.update(connection,retrievedEvent);

				connection.commit();

			} catch (InstanceNotFoundException | AlreadyCanceledEventException | OutOfTimeEventException e) {
				connection.commit();
				throw e;
			} catch (SQLException e) {
				connection.rollback();
				throw new RuntimeException(e);
			} catch (RuntimeException | Error e) {
				connection.rollback();
				throw e;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public List<Reply> findUserReplies(String user_email, Boolean only_yes) throws InputValidationException {
		PropertyValidator.validateMandatoryString("userEmail", user_email);
		try (Connection connection = dataSource.getConnection()) {
			return replyDao.findByEmail(connection, user_email, only_yes);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}




}