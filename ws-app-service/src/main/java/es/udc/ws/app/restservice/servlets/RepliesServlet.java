package es.udc.ws.app.restservice.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.udc.ws.app.restservice.dto.RestReplyDto;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.*;
import es.udc.ws.app.model.reply.Reply;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestReplyDtoConversor;
import es.udc.ws.app.restservice.dto.ReplyToRestReplyDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;

public class RepliesServlet extends RestHttpServletTemplate {

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {

        ServletUtils.checkEmptyPath(req);
        Long eventId = ServletUtils.getMandatoryParameterAsLong(req,"eventId");
        String replyValueString = ServletUtils.getMandatoryParameter(req,"replyValue");
        Boolean replyValue = Boolean.parseBoolean(replyValueString);
        String email = ServletUtils.getMandatoryParameter(req,"email");

        Reply reply;
        try {
            reply = EventServiceFactory.getService().makeReply(eventId, email, replyValue);
        } catch (ReplyOutOfTimeException e) {
            ServletUtils.writeServiceResponse(resp,HttpServletResponse.SC_FORBIDDEN,AppExceptionToJsonConversor.toReplyOutOfTimeException(e),null);
            return;
        } catch (CanceledEventException e) {
            ServletUtils.writeServiceResponse(resp,HttpServletResponse.SC_FORBIDDEN,AppExceptionToJsonConversor.toCanceledEventException(e),null);
            return;
        } catch (AlreadyRepliedException e) {
            ServletUtils.writeServiceResponse(resp,HttpServletResponse.SC_FORBIDDEN,AppExceptionToJsonConversor.toAlreadyRepliedException(e),null);
            return;
        }

        RestReplyDto replyDto = ReplyToRestReplyDtoConversor.toRestReplyDto(reply);
        String replyURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + reply.getReplyId().toString();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", replyURL);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestReplyDtoConversor.toObjectNode(replyDto), headers);
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException {

        ServletUtils.checkEmptyPath(req);
        String user_email = req.getParameter("user_email");
        String only_yes = req.getParameter("replyValue");

        List<Reply> replies = EventServiceFactory.getService().findUserReplies(user_email,
                Boolean.parseBoolean(only_yes));

        List<RestReplyDto> replyDtos = ReplyToRestReplyDtoConversor.toRestReplyDtos(replies);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                JsonToRestReplyDtoConversor.toArrayNode(replyDtos), null);
    }

}
