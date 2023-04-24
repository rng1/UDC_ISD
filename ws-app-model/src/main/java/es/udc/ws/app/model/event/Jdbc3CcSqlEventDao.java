package es.udc.ws.app.model.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class Jdbc3CcSqlEventDao extends AbstractSqlEventDao {
	@Override
	public Event create(Connection connection, Event event) {

		/* Create queryString */
		String queryString = "INSERT INTO Event"
			+ "(name, description, celebrationDate, duration, creationDate, cancelationDate, attendees, absences)"
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS)) {
			/* fill*/
			int i = 1;
			preparedStatement.setString(i++, event.getName());
			preparedStatement.setString(i++, event.getDescription());
			preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCelebrationDate().withNano(0)));
			preparedStatement.setShort(i++, event.getDuration());
			preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCreationDate().withNano(0)));
			preparedStatement.setTimestamp(i++, null);
			preparedStatement.setShort(i++, event.getAttendees());
			preparedStatement.setShort(i++, event.getAbsences());

			/*execute*/
			preparedStatement.executeUpdate();

			/*get id*/
			ResultSet resultSet = preparedStatement.getGeneratedKeys();

			if(!resultSet.next()){
				throw new SQLException("JDBC driver did not return generated key");
			}

			Long eventId = resultSet.getLong(1);

			/*return created event*/
			return new Event(eventId, event.getName(), event.getDescription(), event.getCelebrationDate(),
					event.getCancelationDate(), event.getCreationDate(),
					event.getDuration(), event.getAttendees(), event.getAbsences());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}