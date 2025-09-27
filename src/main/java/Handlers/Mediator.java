package Handlers;

import DTO.SimpleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Mediator implements HandlerController {

    private List<Handler> handlers = List.of(
            new UserLoginHandler(),
            new GetUserStatiHandler(),
            new SetUserStatusHandler());

    @Override
    public SimpleResponse<Object> mediate(Request requestObject, Response responseObject) {


        for(Handler handler: handlers)
        {
            String uri = requestObject.uri();
            String route = handler.HandlableRoute();

            if(handler.HandlableRoute().equals(requestObject.uri()))
            {
                return handler.handle(requestObject, responseObject);


            }
        }

        return new SimpleResponse<>(404, "Not found", null);
    }
}
