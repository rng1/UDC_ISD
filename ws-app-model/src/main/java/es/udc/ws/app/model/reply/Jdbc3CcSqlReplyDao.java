package es.udc.ws.app.model.reply;

import java.sql.*;

public class Jdbc3CcSqlReplyDao extends AbstractSqlReplyDao{

    @Override
    public Reply create(Connection connection, Reply reply) {

        /* Create queryString */
        String queryString = "INSERT INTO Reply"
                + "(eventId, userEmail, replyDate, replyValue)"
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, reply.getEventId());
            preparedStatement.setString(i++, reply.getUserEmail());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(reply.getReplyDate()));
            preparedStatement.setBoolean(i++, reply.getReplyValue());

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();


            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long replyId = resultSet.getLong(1);


            /* Return reply. */
            return new Reply(replyId, reply.getEventId(), reply.getUserEmail(),
                    reply.getReplyDate(), reply.getReplyValue());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }





}
