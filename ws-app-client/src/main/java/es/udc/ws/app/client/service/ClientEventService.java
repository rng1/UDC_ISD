package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.util.List;

public interface ClientEventService{

	public Long addEvent(ClientEventDto event) throws InputValidationException;

	public void cancelEvent(Long eventId) throws InstanceNotFoundException, ClientAlreadyCanceledEventException, ClientOutOfTimeEventException;

	ClientEventDto find(Long eventId) throws InstanceNotFoundException;

	List<ClientEventDto> detailedFind(String endDate, String keywords);

	public ClientReplyDto makeReply (Long eventId, String email, Boolean replyValue) throws
			InstanceNotFoundException, InputValidationException, ClientAlreadyRepliedException, ClientReplyOutOfTimeException, ClientCanceledEventException;

	public List<ClientReplyDto> findUserReplies (String user_email, Boolean only_yes) throws
			InputValidationException;
}