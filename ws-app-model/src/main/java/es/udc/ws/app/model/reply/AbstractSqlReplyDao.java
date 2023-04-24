package es.udc.ws.app.model.reply;


import java.sql.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;


public abstract class AbstractSqlReplyDao implements SqlReplyDao {

    protected AbstractSqlReplyDao() {
    }

    @Override
    public boolean existsReply(Connection connection, Long eventId, String userEmail){

        /* Create "queryString". */
        String queryString = "SELECT eventId, userEmail, replyDate, replyValue" +
                " FROM Reply WHERE eventId = ? AND userEmail = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

                /* Fill "preparedStatement". */
                int i = 1;
                preparedStatement.setLong(i++, eventId);
                preparedStatement.setString(i++, userEmail);

                /* Execute query. */
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return true;
                }
                return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public List<Reply> findByEmail (Connection connection, String userEmail, Boolean only_yes){
        String queryString;
        if (only_yes.equals(true)) {
            queryString = "SELECT *" +
                    "FROM Reply " +
                    "WHERE (userEmail = ?) AND (replyValue = 1)" +
                    "ORDER BY replyDate";
        }
        else {
             queryString = "SELECT *" +
                    "FROM Reply " +
                    "WHERE (userEmail = ?) " +
                    "ORDER BY replyDate";

        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, userEmail);

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            /* Read reservations. */
            List<Reply> replies= new ArrayList<Reply>();

            while (resultSet.next()) {

                i = 1;
                Long replyId = resultSet.getLong(i++);
                Long eventId = resultSet.getLong(i++);
                i++; //como no utilizamos el email, nos lo saltamos
                Timestamp replyDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime replyDate = replyDateAsTimestamp.toLocalDateTime().withNano(0);
                Boolean replyValue = resultSet.getBoolean(i++);


                replies.add(new Reply(replyId, eventId, userEmail, replyDate, replyValue));

            }
            /* Return replies. */
            return replies;

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void remove(Connection connection, Long replyId)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "DELETE FROM Reply WHERE" + " replyId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {


            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, replyId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(replyId,
                        Reply.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }



}