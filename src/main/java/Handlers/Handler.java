package Handlers;

import DTO.SimpleResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import spark.Request;
import spark.Response;

public interface Handler {
    public final String WEB_GET_REQUEST = "GET";
    public final String WEB_POST_REQUEST = "POST";
    public final String WEB_DELETE_REQUEST = "DELETE";
    public final String WEB_PUT_REQUEST = "PUT";


    public SimpleResponse<Object> handle(Request requestObject, Response responseObject);

    public String HandlableRoute();

    public String HandlableMethod();
}
