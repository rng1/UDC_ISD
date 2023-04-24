package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.*;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.time.LocalDateTime;
import java.util.List;

public class ThriftClientEventService implements ClientEventService{
	private final static String ENDPOINT_ADDRESS_PARAMETER = "ThriftClientEventService.endpointAddress";

	private final static String endpointAddress = ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);

	@Override
	public Long addEvent(ClientEventDto event) throws InputValidationException {
		ThriftEventService.Client client = getClient();
		TTransport transport = client.getInputProtocol().getTransport();

		try {
			transport.open();

			return client.addEvent(ClientEventDtoToThriftEventDtoConversor.toThriftEventDto(event)).getEventId();
		} catch (ThriftInputValidationException e) {
			throw new InputValidationException(e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			transport.close();
		}
	}

	@Override
	public List<ClientEventDto> detailedFind(String endDate, String keywords){
		ThriftEventService.Client client = getClient();
		TTransport transport = client.getInputProtocol().getTransport();
		String endDateTime = endDate + "T00:00";
		try {
			transport.open();

			return ClientEventDtoToThriftEventDtoConversor.toClientEventDtos(client.findEvents(endDateTime, keywords));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			transport.close();
		}
	}

	@Override
	public ClientEventDto find(Long eventId) throws InstanceNotFoundException{
		ThriftEventService.Client client = getClient();
		TTransport transport = client.getInputProtocol().getTransport();

		try {
			transport.open();

			return ClientEventDtoToThriftEventDtoConversor.toClientEventDto(client.findEvent(eventId));
		} catch (ThriftInstanceNotFoundException e){
			throw new InstanceNotFoundException(e.getInstanceId(),e.getInstanceType());
		} catch (Exception e){
			throw new RuntimeException(e);
		} finally {
			transport.close();
		}
	}

	@Override
	public ClientReplyDto makeReply(Long eventId, String email, Boolean replyValue) throws InstanceNotFoundException, InputValidationException, ClientAlreadyRepliedException, ClientReplyOutOfTimeException, ClientCanceledEventException{
		ThriftEventService.Client client = getClient();
		TTransport transport = client.getInputProtocol().getTransport();

		try {

			transport.open();

			return ClientReplyDtoToThriftReplyDtoConversor.toClientReplyDto(client.makeReply(eventId,email,replyValue));

		} catch (ThriftInstanceNotFoundException e){
			throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
		} catch (ThriftInputValidationException e){
			throw new InputValidationException(e.getMessage());
		} catch (ThriftAlreadyRepliedException e){
			throw new ClientAlreadyRepliedException(e.getEventId(),e.getUserEmail());
		} catch (ThriftReplyOutOfTimeException e){
			throw new ClientReplyOutOfTimeException(LocalDateTime.parse(e.getCelebrationDate()));
		} catch (ThriftCanceledEventException e){
			throw new ClientCanceledEventException(e.getEventId());
		} catch (Exception e){
			throw new RuntimeException(e);
		} finally {
			transport.close();
		}

	}


	@Override
	public void cancelEvent(Long eventId) throws InstanceNotFoundException, ClientAlreadyCanceledEventException, ClientOutOfTimeEventException{
		ThriftEventService.Client client = getClient();
		TTransport transport = client.getInputProtocol().getTransport();

		try  {
			transport.open();

			client.cancelEvent(eventId);
		} catch (ThriftInstanceNotFoundException e) {
			throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
		} catch (ThriftAlreadyCanceledEventException e) {
			throw new ClientAlreadyCanceledEventException(e.getEventId());
		} catch (ThriftOutOfTimeEventException e) {
			throw new ClientOutOfTimeEventException(e.getEventId(), LocalDateTime.parse(e.getCelebrationDate()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			transport.close();
		}
	}


	@Override
	public List<ClientReplyDto> findUserReplies(String user_email, Boolean only_yes) throws InputValidationException{
		ThriftEventService.Client client = getClient();
		TTransport transport = client.getInputProtocol().getTransport();

		try {
			transport.open();
			return ClientReplyDtoToThriftReplyDtoConversor.toClientReplyDtosList(client.findUserReplies(user_email, only_yes));
		} catch (ThriftInputValidationException e) {
			throw new InputValidationException(e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			transport.close();
		}
	}

	private ThriftEventService.Client getClient() {
		try {
			TTransport transport = new THttpClient(endpointAddress);
			TProtocol protocol = new TBinaryProtocol(transport);

			return new ThriftEventService.Client(protocol);
		} catch (TTransportException e) {
			throw new RuntimeException(e);
		}
	}
}