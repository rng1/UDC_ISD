namespace java es.udc.ws.app.thrift

struct ThriftEventDto {
	1: i64 eventId
	2: string name
	3: string description
	4: string celebrationDate
	5: string cancelationDate
	6: i16 duration
	7: i16 attendees
	8: i16 totalReplies
}

struct ThriftReplyDto {
    1: i64 replyId
    2: i64 eventId
    3: string userEmail
    4: bool replyValue
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftAlreadyRepliedException {
    1: i64 eventId
    2: string userEmail
}

exception ThriftAlreadyCanceledEventException {
    1: i64 eventId
}

exception ThriftCanceledEventException {
    1: i64 eventId
}

exception ThriftOutOfTimeEventException {
    1: i64 eventId
    2: string celebrationDate
}

exception ThriftReplyOutOfTimeException {
    1: string celebrationDate
}

service ThriftEventService {
    ThriftEventDto addEvent(1: ThriftEventDto eventDto) throws (1: ThriftInputValidationException e)
    list<ThriftEventDto> findEvents(1: string endDate, 2: string keywords)
    ThriftEventDto findEvent (1: i64 eventId) throws (1: ThriftInstanceNotFoundException e)
    ThriftReplyDto makeReply (1: i64 eventId, 2: string email, 3: bool replyValue) throws (1: ThriftInstanceNotFoundException e, 2: ThriftInputValidationException ee, 3: ThriftAlreadyRepliedException eee, 4: ThriftReplyOutOfTimeException eeee, 5: ThriftCanceledEventException eeeee)
    void cancelEvent(1: i64 eventId) throws (1: ThriftAlreadyCanceledEventException e, 2: ThriftOutOfTimeEventException ee,
     3: ThriftInstanceNotFoundException eee)
    list<ThriftReplyDto> findUserReplies(1: string user_email, 2: bool only_yes) throws (1: ThriftInputValidationException e)
}