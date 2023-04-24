package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.thrift.ThriftReplyDto;

import java.util.ArrayList;
import java.util.List;

public class ClientReplyDtoToThriftReplyDtoConversor {




    public static List<ClientReplyDto> toClientReplyDtosList(List<ThriftReplyDto> replies) {
        List<ClientReplyDto> clientReplyDtos = new ArrayList<>(replies.size());
        
        for (ThriftReplyDto reply : replies) {
            clientReplyDtos.add(toClientReplyDto(reply));
        }

        return clientReplyDtos;
    }

    public static ClientReplyDto toClientReplyDto(ThriftReplyDto reply) {

        return new ClientReplyDto(
                reply.getReplyId(),
                reply.getEventId(),
                reply.getUserEmail(),
                reply.isReplyValue()
        );
    }
}
