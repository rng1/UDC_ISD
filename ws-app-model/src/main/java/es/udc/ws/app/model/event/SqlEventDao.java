package es.udc.ws.app.model.event;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;


import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface SqlEventDao {

	public Event create(Connection connection, Event event);

	public Event find(Connection connection, Long eventId)
			throws InstanceNotFoundException;

	public List<Event> detailedFind(Connection connection, LocalDateTime startDate,
									  LocalDateTime endDate, String keywords);
	
	public void update(Connection connection, Event event)
		throws InstanceNotFoundException;

	public void remove(Connection connection, Long eventId)
		throws InstanceNotFoundException;
}