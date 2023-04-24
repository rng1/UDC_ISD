package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.reply.Reply;
import es.udc.ws.app.thrift.ThriftReplyDto;

import java.util.ArrayList;
import java.util.List;

public class ReplyToThriftReplyDtoConversor {

    public static ThriftReplyDto toThriftReplyDto(Reply reply) {
        return new ThriftReplyDto(reply.getReplyId(), reply.getEventId(), reply.getUserEmail(), reply.getReplyValue());
    }

    public static List<ThriftReplyDto> toThriftReplyListDto (List<Reply> replies) {
        List<ThriftReplyDto> replyDtos = new ArrayList<>(replies.size());

        for (Reply reply : replies) {
            replyDtos.add(toThriftReplyDto(reply));
        }

        return replyDtos;
    }
}
