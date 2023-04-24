package es.udc.ws.app.model.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

/**
 * A partial implementation of
 * <code>SQLEventDAO</code> that leaves
 * <code>create(Connection, Event)</code> as abstract.
 */

public abstract class AbstractSqlEventDao implements SqlEventDao {

	protected AbstractSqlEventDao() {
	}

    @Override
    public Event find(Connection connection, Long eventId)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "SELECT eventId, name, description, celebrationDate," +
                " duration, creationDate, cancelationDate, attendees, absences" +
                " FROM Event WHERE eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i, eventId);

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(eventId,
                        Event.class.getName());
            }

            /* Get results. */
            i = 2;
            String name = resultSet.getString(i++);
            String description = resultSet.getString(i++);
            Timestamp celebrationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime celebrationDate = celebrationDateAsTimestamp.toLocalDateTime().withNano(0);
            short duration = resultSet.getShort(i++);
            Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime().withNano(0);
            Timestamp cancelationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime cancelationDate = null;
            if (cancelationDateAsTimestamp != null) {
                cancelationDate = cancelationDateAsTimestamp.toLocalDateTime().withNano(0);
            }
            short attendees = resultSet.getShort(i++);
            short absentees = resultSet.getShort(i++);

            /* Return event. */
            return new Event(eventId, name, description, celebrationDate,
            cancelationDate,creationDate, duration,attendees,absentees);

        } catch (SQLException e) {
            throw new InstanceNotFoundException(eventId, Event.class.getName());
        }

    }

    @Override
    public List<Event> detailedFind(Connection connection, LocalDateTime startDate,
                                    LocalDateTime endDate, String keywords) {
        LocalDateTime startDateFormat = null;
        LocalDateTime endDateFormat = null;

        /* Create "queryString". */
        String[] words = keywords != null ? keywords.split(" ") : null;

        String queryString = "SELECT eventId, name, description, celebrationDate," +
                " duration, creationDate, cancelationDate, attendees, absences" +
                " FROM Event WHERE celebrationDate>=?";


        if (startDate == null || startDate.isBefore(LocalDateTime.now().withNano(0).plusHours(24))) {
            startDateFormat = LocalDateTime.now().withNano(0).plusHours(24);
        }
        else {
            startDateFormat = startDate.withNano(0);
        }

        if (endDate != null) {
            queryString += " AND celebrationDate <=?";
            endDateFormat = endDate.withNano(0);
        }

        if (words != null && words.length > 0) {
            for (int i = 0; i < words.length; i++) {
                queryString += " AND LOWER(description) LIKE LOWER(?)";
            }
        }
        queryString += " ORDER BY celebrationDate";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            if (words != null) {
                /* Fill "preparedStatement". */
                for (int i = 0; i < words.length; i++) {
                    preparedStatement.setString(i + 3, "%" + words[i] + "%");
                }
            }

            int i = 1;
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(startDateFormat));
            if(endDate != null){
                preparedStatement.setTimestamp(i, Timestamp.valueOf(endDateFormat));
            }

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            /* Read movies. */
            List<Event> events = new ArrayList<>();

            while (resultSet.next()) {

                i = 1;
                Long eventId = resultSet.getLong(i++);
                String name = resultSet.getString(i++);
                String description = resultSet.getString(i++);
                Timestamp celebrationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime celebrationDate = celebrationDateAsTimestamp.toLocalDateTime();
                short duration = resultSet.getShort(i++);
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
                Timestamp cancelationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime cancelationDate = null;
                if (cancelationDateAsTimestamp != null) {
                    cancelationDate = cancelationDateAsTimestamp.toLocalDateTime().withNano(0);
                }
                short attendees = resultSet.getShort(i++);
                short absentees = resultSet.getShort(i++);

                events.add(new Event(eventId, name, description, celebrationDate,
                        cancelationDate, creationDate,duration, attendees, absentees));

            }

            /* Return events. */
            return events;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Connection connection, Event event) throws InstanceNotFoundException {
        String queryString = "UPDATE Event" +
                             " SET name = ?, description = ?, celebrationDate = ?," +
                             " duration = ?, cancelationDate = ?, attendees = ?, absences = ? WHERE eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setString(i++, event.getName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCelebrationDate()));
            preparedStatement.setShort(i++, event.getDuration());
            if (event.getCancelationDate() == null){
                preparedStatement.setTimestamp(i++,null);
            } else {
                preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCancelationDate()));
            }
            preparedStatement.setShort(i++, event.getAttendees());
            preparedStatement.setShort(i++, event.getAbsences());
            preparedStatement.setLong(i++, event.getEventId());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0){
                throw new InstanceNotFoundException(event.getEventId(),Event.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long eventId) throws InstanceNotFoundException {
        String queryString = "DELETE FROM Event WHERE eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, eventId);
            int removedRows = preparedStatement.executeUpdate();
            if (removedRows == 0) {
                throw new InstanceNotFoundException(eventId, Event.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}