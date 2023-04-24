package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.ClientEventServiceFactory;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.util.exceptions.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class AppServiceClient {
    public static void main(String[] args) {
        if(args.length == 0){
            printUsageAndExit();
        }

        ClientEventService clientEventService = ClientEventServiceFactory.getService();

        if("-addEvent".equalsIgnoreCase(args[0])) {

            validateArgs(args, 5, new int[] {});
            try{
                int dateStartInSecs = (int) LocalDateTime.parse(args[3]).toEpochSecond(ZoneOffset.UTC);
                int dateEndInSecs = (int) LocalDateTime.parse(args[4]).toEpochSecond(ZoneOffset.UTC);
                int durationInSecs =  dateEndInSecs - dateStartInSecs;
                int durationInHours = durationInSecs / 3600;

                ClientEventDto eventDto = new ClientEventDto(args[1],args[2],args[3],(short) durationInHours);
                Long eventId = clientEventService.addEvent(eventDto);

                System.out.println("Event created succesfully | ID: " + eventId);
            } catch (NumberFormatException | InputValidationException ex){
                ex.printStackTrace(System.err);
            } catch (Exception ex){
                ex.printStackTrace(System.err);
            }

        } else if("-respond".equalsIgnoreCase(args[0])){
            validateArgs(args, 4, new int [] {2});

            try{
                Long eventId = Long.parseLong(args[2]);
                Boolean replyValue = Boolean.parseBoolean(args[3]);
                ClientReplyDto reply = clientEventService.makeReply(eventId,args[1],replyValue);

                System.out.println("Response created succesfully | ID: " +reply.getReplyId()+ " Event: "+eventId+" Email: "+args[1]+ " ReplyValue: "+replyValue);
            } catch (ClientAlreadyRepliedException | ClientCanceledEventException | ClientReplyOutOfTimeException ex){
                ex.printStackTrace(System.err);
            } catch (Exception ex){
                ex.printStackTrace(System.err);
            }

        } else if("-cancel".equalsIgnoreCase(args[0])) {

            validateArgs(args, 2, new int[] {1});

            try{
                Long eventId = Long.parseLong(args[1]);
                clientEventService.cancelEvent(eventId);

                System.out.println("Event canceled succesfully | ID: " + eventId);
            } catch (NumberFormatException | ClientAlreadyCanceledEventException | ClientOutOfTimeEventException ex){
                ex.printStackTrace(System.err);
            } catch (Exception ex){
                ex.printStackTrace(System.err);
            }


        } else if("-findEvent".equalsIgnoreCase(args[0])) {
//          [Find event by ID]    EventServiceClient -findEvent <eventId>
            validateArgs(args, 2, new int[] {1});

            try {
                Long eventId = Long.parseLong(args[1]);
                ClientEventDto event = clientEventService.find(eventId);

                System.out.println("Id: " + event.getEventId() +
                        ", Name: " + event.getName() +
                        ", Description: " + event.getDescription() +
                        ", Celebration date: " + event.getCelebrationDate() +
                        ", Cancellation date: " + event.getCancelationDate() +
                        ", Duration: " + event.getDuration() + " days" +
                        ", Attendees: " + event.getAttendees() +
                        ", Total replies: " + event.getTotalReplies());
            } catch (InstanceNotFoundException e) {
                e.printStackTrace(System.err);
            } catch (Exception ex){
                ex.printStackTrace(System.err);
            }

        } else if("-findEvents".equalsIgnoreCase(args[0])) {
//          [Find events]         EventServiceClient -findEvents <untilDate> [<keyword>]

            int index = 2;
            int expectedArgs = index < args.length ? 3 : 2;

            validateArgs(args, expectedArgs, new int[] {});

            try {
                List<ClientEventDto> events;
                if (expectedArgs == 3)
                    events = clientEventService.detailedFind(args[1], args[2]);
                else
                    events = clientEventService.detailedFind(args[1], null);

                System.out.print("Found " + events.size() + " event(s)");
                if (expectedArgs == 3) System.out.println(" with keywords '" + args[2] + "'");

                for (ClientEventDto event : events) {
                    System.out.println("Id: " + event.getEventId() +
                            ", Name: " + event.getName() +
                            ", Description: " + event.getDescription() +
                            ", Celebration date: " + event.getCelebrationDate() +
                            ", Cancellation date: " + event.getCancelationDate() +
                            ", Duration: " + event.getDuration() + " days" +
                            ", Attendees: " + event.getAttendees() +
                            ", Total replies: " + event.getTotalReplies());
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if("-findResponses".equalsIgnoreCase(args[0])) {

            validateArgs(args,3 , new int[] {});
            try {
                List<ClientReplyDto> replies;

                Boolean only_yes = Boolean.parseBoolean(args[2]);

                replies = clientEventService.findUserReplies(args[1],only_yes);

                System.out.println("Found " + replies.size() + " response(s)");

                for (ClientReplyDto reply : replies) {
                    System.out.println("ReplyId: " + reply.getReplyId() +
                            ", EventId: " + reply.getEventId() +
                            ", UserEmail: " + reply.getUserEmail() +
                            ", Value: " + reply.getReplyValue());
                }
            } catch (InputValidationException e) {
                e.printStackTrace(System.err);
            } catch (Exception ex){
                ex.printStackTrace(System.err);
            }


        } else {
            System.out.println("\nUnknown args\n\n");
            printUsageAndExit();
        }


    }


    public static void validateArgs(String[] args, int expectedArgs, int[] numericArgs){
        if(expectedArgs != args.length){
            printUsageAndExit();
        }

        for (int pos : numericArgs) {
            try {
                Double.parseDouble(args[pos]);
            } catch (NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }

    public static void printUsageAndExit(){
        printUsage();
        System.exit(-1);
    }

    public static void printUsage(){
        System.err.println("""
                Usage:
                    [Add event]           EventServiceClient -addEvent <name> <description> <start_date> <end_date>
                    [Respond to event]    EventServiceClient -respond <userEmail> <eventId> <replyValue>
                    [Cancel event]        EventServiceClient -cancel <eventId>
                    [Find event by ID]    EventServiceClient -findEvent <eventId>
                    [Find events]         EventServiceClient -findEvents <untilDate> [<keyword>]
                    [Find user responses] EventServiceClient -findResponses <userEmail> <onlyAffirmative>
                """);
    }

}