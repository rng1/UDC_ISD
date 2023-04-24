package es.udc.ws.app.model.reply;

import java.sql.Connection;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface SqlReplyDao {

    public Reply create(Connection connection, Reply reply);

    public List<Reply> findByEmail(Connection connection, String userEmail, Boolean only_yes);

    public boolean existsReply(Connection connection, Long eventId, String userEmail);

    public void remove(Connection connection, Long replyId)
            throws InstanceNotFoundException;

}
