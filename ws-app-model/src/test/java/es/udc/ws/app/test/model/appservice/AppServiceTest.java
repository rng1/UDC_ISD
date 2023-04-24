package es.udc.ws.app.test.model.appservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.udc.ws.app.model.eventservice.exceptions.*;
import es.udc.ws.app.model.reply.Reply;
import es.udc.ws.app.model.reply.SqlReplyDao;
import es.udc.ws.app.model.reply.SqlReplyDaoFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
import es.udc.ws.app.model.eventservice.EventService;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import static es.udc.ws.app.model.util.ModelConstants.*;


public class AppServiceTest {
	private final long NON_EXISTENT_EVENT_ID = -1;
	private final long NON_EXISTENT_EVENT_ID2 = 9999;
	private final String EMAIL = "myemail@udc.es";

	private static EventService eventService = null;
	private static SqlEventDao eventDao = null;
	private static SqlReplyDao replyDao = null;

	@BeforeAll
	public static void init() {
		DataSource dataSource = new SimpleDataSource();
		DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);

		eventService = EventServiceFactory.getService();
		eventDao = SqlEventDaoFactory.getDao();
		replyDao = SqlReplyDaoFactory.getDao();
	}

	private Event getValidEvent(String name, LocalDateTime date, short duration) {
		return new Event(name, "description", date, duration);
	}

	private Event getValidEvent(String name) {
		return new Event(name, "description",
				LocalDateTime.now().withNano(0).plusDays(2), (short) 4);
	}

	private Event getValidEvent() {
		return getValidEvent("name2");
	}

	private Event getInvalidTimeEvent(){
		return new Event("Invalid Time Event", "desc",
				LocalDateTime.now().withNano(0).minusDays(1), (short) 4);
	}


	private Event createEvent(Event event) {

		Event addedEvent = null;
		try {
			addedEvent = eventService.addEvent(event);
		} catch (InputValidationException e) {
			throw new RuntimeException(e);
		}
		return addedEvent;

	}

	private Event createEventFromDao(Event event){
		DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
		event.setCreationDate(LocalDateTime.now().withNano(0));
		event.setCancelationDate(null);

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

	private void removeEvent(Long eventId) {
		DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

		try (Connection connection = dataSource.getConnection()) {
			try {
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				connection.setAutoCommit(false);

				eventDao.remove(connection, eventId);

				connection.commit();
			} catch (InstanceNotFoundException e) {
				connection.commit();
				throw new RuntimeException(e);
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
	private void removeReply(Long replyId) {
		DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

		try (Connection connection = dataSource.getConnection()) {
			try {
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				connection.setAutoCommit(false);

				replyDao.remove(connection, replyId);

				connection.commit();
			} catch (InstanceNotFoundException e) {
				connection.commit();
				throw new RuntimeException(e);
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



	@Test
	public void testCreateEventAndFindEvent() throws InputValidationException {
		Event event = getValidEvent();
		Event addedEvent = null;

		try {
			LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);
			addedEvent = eventService.addEvent(getValidEvent());
			LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);

			Event foundEvent = eventService.find(addedEvent.getEventId());

			assertEquals(addedEvent, foundEvent);
			assertEquals(foundEvent.getName(),event.getName());
			assertEquals(foundEvent.getDuration(),event.getDuration());
			assertEquals(foundEvent.getDescription(),event.getDescription());
			assertEquals(foundEvent.getCelebrationDate(),event.getCelebrationDate());
			assertEquals(foundEvent.getCancelationDate(),event.getCancelationDate());
			assertTrue((foundEvent.getCreationDate().compareTo(beforeCreationDate) >= 0)
					&& (foundEvent.getCreationDate().compareTo(afterCreationDate) <= 0));
			assertEquals(foundEvent.getAttendees(), event.getAttendees());
			assertEquals(foundEvent.getAbsences(), event.getAbsences());
		} catch (InstanceNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (addedEvent != null) {
				removeEvent(addedEvent.getEventId());
			}
		}
	}

	@Test
	public void testFindNonExistentEvent() {
		assertThrows(InstanceNotFoundException.class, () -> eventService.find(NON_EXISTENT_EVENT_ID));
	}

	@Test
	public void testFindEventsByDates(){
		List<Event> events = new LinkedList<Event>();
		Event event1 = createEvent(getValidEvent("event name 1",
				LocalDateTime.now().withNano(0).plusDays(3), (short) 36));
		events.add(event1);
		Event event2 = createEvent(getValidEvent("event name 2",
				LocalDateTime.now().withNano(0).plusDays(4), (short) 8));
		events.add(event2);
		Event event3 = createEvent(getValidEvent("event name 3",
				LocalDateTime.now().withNano(0).plusDays(6), (short) 4));
		events.add(event3);

		try{
			List<Event> foundEvents = eventService.detailedFind(LocalDateTime.now().withNano(0),
					LocalDateTime.now().withNano(0).plusDays(8), null);
			assertEquals(events, foundEvents);

			foundEvents = eventService.detailedFind(LocalDateTime.now().withNano(0).plusDays(5),
					LocalDateTime.now().withNano(0).plusDays(8),  null);
			assertEquals(1, foundEvents.size());
			assertEquals(events.get(2), foundEvents.get(0));

			foundEvents = eventService.detailedFind(LocalDateTime.now().withNano(0),
					LocalDateTime.now().withNano(0).plusDays(2),
					null);
			assertEquals(0, foundEvents.size());
		} finally {
			for (Event event : events){
				removeEvent(event.getEventId());
			}
		}

	}

	@Test
	public void testCreateInvalidEventName() {
		assertThrows(InputValidationException.class, () -> {
			Event invalidEvent = getValidEvent();
			invalidEvent.setName(null);
			Event addedEvent = eventService.addEvent(invalidEvent);
			removeEvent(addedEvent.getEventId());
		});
	}

	@Test
	public void testCreateInvalidEventDescription() {
		assertThrows(InputValidationException.class, () -> {
			Event invalidEvent = getValidEvent();
			invalidEvent.setDescription(null);
			Event addedEvent = eventService.addEvent(invalidEvent);
			removeEvent(addedEvent.getEventId());
		});
	}

	@Test
	public void testCreateEventInvalidDate() {
		assertThrows(InputValidationException.class, () -> {
			Event addedEvent = eventService.addEvent(getInvalidTimeEvent());
			removeEvent(addedEvent.getEventId());
		});
	}

	@Test
	public void testCreateInvalidEventDuration() {
		assertThrows(InputValidationException.class, () -> {
			Event invalidEvent = getValidEvent();
			invalidEvent.setDuration((short) -1);
			Event addedEvent = eventService.addEvent(invalidEvent);
			removeEvent(addedEvent.getEventId());
		});
	}

	@Test
	public void testCancelEvent() throws InputValidationException, InstanceNotFoundException,AlreadyCanceledEventException, OutOfTimeEventException {
		Event addedEvent = null;
		try{
			addedEvent = eventService.addEvent(getValidEvent());
			eventService.cancelEvent(addedEvent.getEventId());
			Event foundEvent = eventService.find(addedEvent.getEventId());
			assertNotNull(foundEvent.getCancelationDate());
		} finally {
			if (addedEvent != null){
				removeEvent(addedEvent.getEventId());
			}
		}
	}

	@Test
	public void testCancelNotFoundEvent(){
		assertThrows(InstanceNotFoundException.class, () -> {
			Event addedEvent = eventService.addEvent(getValidEvent());
			removeEvent(addedEvent.getEventId());
			eventService.cancelEvent(addedEvent.getEventId());
		});
	}

	@Test
	public void testCancelCanceledEvent() throws InputValidationException{
		Event addedEvent = eventService.addEvent(getValidEvent());
		assertThrows(AlreadyCanceledEventException.class, () -> {
			eventService.cancelEvent(addedEvent.getEventId());
			eventService.cancelEvent(addedEvent.getEventId());
		});
		removeEvent(addedEvent.getEventId());
	}

	@Test
	public void testCancelOutOfTimeEvent(){
		Event createdEvent = createEventFromDao(getInvalidTimeEvent());
		assertThrows(OutOfTimeEventException.class, () -> {
			eventService.cancelEvent(createdEvent.getEventId());
		});
		removeEvent(createdEvent.getEventId());
	}

	@Test
	public void testReply() throws InputValidationException, InstanceNotFoundException, AlreadyRepliedException, ReplyOutOfTimeException, CanceledEventException{
		Event event = createEvent(getValidEvent());
		Reply createdReply = null;
		try{
			LocalDateTime timeBefore = LocalDateTime.now().withNano(0);
			createdReply = eventService.makeReply(event.getEventId(),EMAIL, true);
			LocalDateTime timeAfter = LocalDateTime.now().withNano(0);
			
			List<Reply> replies = eventService.findUserReplies(createdReply.getUserEmail(),true);
			Reply foundReply = replies.get(0);

			assertEquals(createdReply.getEventId(), foundReply.getEventId());
			assertEquals(createdReply.getReplyId(), foundReply.getReplyId());
			assertEquals(createdReply.getReplyValue(), foundReply.getReplyValue());
			assertEquals(createdReply.getUserEmail(), foundReply.getUserEmail());
			assertEquals(createdReply.getReplyDate(), foundReply.getReplyDate());
			assertTrue((createdReply.getReplyDate().compareTo(timeBefore) >= 0) || (createdReply.getReplyDate().compareTo(timeAfter) <= 0));
		}finally{
			if (createdReply != null){
				removeReply(createdReply.getReplyId());
			}
			removeEvent(event.getEventId());
		}
	}

	@Test
	public void testNegativeIdEventReply() {
		assertThrows(InputValidationException.class,() -> eventService.makeReply(NON_EXISTENT_EVENT_ID,EMAIL,true));
	}
	@Test
	public void testNonExistentEventReply() {
		assertThrows(InstanceNotFoundException.class,() -> eventService.makeReply(NON_EXISTENT_EVENT_ID2,EMAIL,true));
	}
	@Test
	public void testNullEventReply(){
		assertThrows(InputValidationException.class,() -> eventService.makeReply(null,EMAIL,true));
	}
	@Test
	public void testEmptyEmailReply(){
		Event event = createEvent(getValidEvent());
		assertThrows(InputValidationException.class,() -> eventService.makeReply(event.getEventId(),"",true));
		removeEvent(event.getEventId());
	}
	@Test
	public void testEmptyValueReply(){
		Event event = createEvent(getValidEvent());
		assertThrows(InputValidationException.class,() -> eventService.makeReply(event.getEventId(),EMAIL,null));
		removeEvent(event.getEventId());
	}

	@Test
	public void testOutOfTimeReply(){
		Event event = createEventFromDao(getInvalidTimeEvent());
		assertThrows(ReplyOutOfTimeException.class,() -> eventService.makeReply(event.getEventId(),EMAIL,true));
		removeEvent(event.getEventId());
	}

	@Test
	public void testAlreadyRepliedException() throws InstanceNotFoundException, InputValidationException, AlreadyRepliedException, ReplyOutOfTimeException, CanceledEventException{
		Event event = createEvent(getValidEvent());
		eventService.makeReply(event.getEventId(),EMAIL,true);
		assertThrows(AlreadyRepliedException.class, () -> eventService.makeReply(event.getEventId(),EMAIL,true));
		removeEvent(event.getEventId());
	}

	@Test
	public void testFindRepliesByEmail()  throws InstanceNotFoundException, InputValidationException, AlreadyRepliedException, ReplyOutOfTimeException, CanceledEventException {

		// Add events
		List<Reply> replies = new LinkedList<Reply>();
		List<Reply> trueReplies = new LinkedList<Reply>();
		Event event = createEvent(getValidEvent("Event 1"));
		Event event2 = createEvent(getValidEvent("Event 2"));
		Event event3 = createEvent(getValidEvent("Event 3"));

		Reply reply1 = eventService.makeReply(event.getEventId(),EMAIL, true);
		Reply reply2 = eventService.makeReply(event2.getEventId(),EMAIL, false);
		Reply reply3 = eventService.makeReply(event3.getEventId(),EMAIL, true);

		replies.add(reply1);
		replies.add(reply2);
		replies.add(reply3);
		trueReplies.add(reply1);
		trueReplies.add(reply3);

		try {
			List<Reply> foundAllReplies = eventService.findUserReplies(EMAIL, false);
			List<Reply> foundTrueReplies = eventService.findUserReplies(EMAIL, true);
			assertEquals(3, foundAllReplies.size());
			assertEquals(2, foundTrueReplies.size());
			assertEquals(replies, foundAllReplies);
			assertEquals(trueReplies, foundTrueReplies);
		} finally {
			removeReply(reply1.getReplyId());
			removeReply(reply2.getReplyId());
			removeReply(reply3.getReplyId());
			removeEvent(event.getEventId());
			removeEvent(event2.getEventId());
		}
	}

	@Test
	public void testFindRepliesEmptyEmail(){
		assertThrows(InputValidationException.class,() -> eventService.findUserReplies("", true));
	}
	@Test
	public void testFindRepliesNonExistent() throws InputValidationException{
		List<Reply> replies = new LinkedList<Reply>();
		replies = eventService.findUserReplies("empty", false);
		assertEquals(0, replies.size());
	}
}