package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.app.client.service.rest.json.*;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RestClientEventService implements ClientEventService {
	private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientEventService.endpointAddress";
	private String endpointAddress;

	@Override
	public Long addEvent(ClientEventDto event) throws InputValidationException {

		try{
			HttpResponse response = Request.Post(getEndpointAddress() + "events").
				bodyStream(toInputStream(event), ContentType.create("application/json")).
				execute().returnResponse();

			validateStatusCode(HttpStatus.SC_CREATED, response);

			return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent()).getEventId();

		} catch (InputValidationException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void cancelEvent(Long eventId) throws InstanceNotFoundException,
			ClientAlreadyCanceledEventException, ClientOutOfTimeEventException {
		try{
			HttpResponse response = Request.Post(getEndpointAddress() + "events/cancel").bodyForm(
				Form.form()
					.add("eventId", Long.toString(eventId))
				.build())
			.execute().returnResponse();

			validateStatusCode(HttpStatus.SC_NO_CONTENT, response);
		} catch (InstanceNotFoundException | ClientAlreadyCanceledEventException | ClientOutOfTimeEventException e){
			throw e;
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public ClientEventDto find(Long eventId) throws InstanceNotFoundException {
		try {
			HttpResponse response = Request.Get(getEndpointAddress() + "events/" + eventId).
					execute().returnResponse();
			validateStatusCode(HttpStatus.SC_OK, response);
			return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent());
		} catch (InstanceNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ClientEventDto> detailedFind(String endDate, String keywords) {

		String keywordsQuery = "";
		if (keywords != null)
			keywordsQuery = "&keywords=" + URLEncoder.encode(keywords, StandardCharsets.UTF_8);

		try {
			HttpResponse response = Request.Get(getEndpointAddress() + "events?endDate="
							+ URLEncoder.encode(endDate, StandardCharsets.UTF_8) + keywordsQuery).
					execute().returnResponse();

			validateStatusCode(HttpStatus.SC_OK, response);

			return JsonToClientEventDtoConversor.toClientEventDtos(response.getEntity().getContent());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	@Override
	public ClientReplyDto makeReply (Long eventId, String email, Boolean replyValue) throws
			InstanceNotFoundException, InputValidationException, ClientAlreadyRepliedException, ClientReplyOutOfTimeException, ClientCanceledEventException {

		try {
			HttpResponse response = Request.Post(getEndpointAddress() + "replies").
					bodyForm(
							Form.form().
									add("eventId", Long.toString(eventId)).
									add("email", email).
									add("replyValue", Boolean.toString(replyValue)).
									build()).
					execute().returnResponse();

			validateStatusCode(HttpStatus.SC_CREATED, response);

			return JsonToClientReplyDtoConversor.toClientReplyDto(
					response.getEntity().getContent());

		} catch (InstanceNotFoundException | InputValidationException | ClientAlreadyRepliedException | ClientReplyOutOfTimeException | ClientCanceledEventException e){
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ClientReplyDto> findUserReplies (String user_email, Boolean only_yes) throws
			InputValidationException{

		String only_yes_query;
		String only_yes_string = only_yes.toString();

		if (!only_yes) only_yes_query = "";
		else {only_yes_query = "&replyValue=" + URLEncoder.encode(only_yes_string, StandardCharsets.UTF_8);}

		try {
			HttpResponse response = Request.Get(getEndpointAddress() + "replies?user_email="
							+ URLEncoder.encode(user_email, StandardCharsets.UTF_8) + only_yes_query).
					execute().returnResponse();

			validateStatusCode(HttpStatus.SC_OK, response);

			return JsonToClientReplyDtoConversor.toClientReplyDtos(response.getEntity()
					.getContent());

		}catch (InputValidationException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private synchronized String getEndpointAddress(){
		if (endpointAddress == null) {
	        endpointAddress = ConfigurationParametersManager
	            .getParameter(ENDPOINT_ADDRESS_PARAMETER);
	    }
	    return endpointAddress;
    }

    private InputStream toInputStream(ClientEventDto event){

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientEventDtoConversor.toObjectNode(event));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void validateStatusCode(int successCode, HttpResponse response) throws Exception {

        try {

            int statusCode = response.getStatusLine().getStatusCode();

            /* Success? */
            if (statusCode == successCode) {
                return;
            }

            /* Handler error. */
            switch (statusCode) {

                case HttpStatus.SC_NOT_FOUND:
                    throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                            response.getEntity().getContent());

                case HttpStatus.SC_BAD_REQUEST:
                    throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                            response.getEntity().getContent());

                case HttpStatus.SC_FORBIDDEN:
                    throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                            response.getEntity().getContent());

                default:
                    throw new RuntimeException("HTTP error; status code = "
                            + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}