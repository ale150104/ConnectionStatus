package Handlers;

import DTO.SimpleResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import spark.Request;
import spark.Response;

public interface Handler {

    public SimpleResponse<Object> handle(Request requestObject, Response responseObject);

    public String HandlableRoute();
}
