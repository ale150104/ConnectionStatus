package Handlers;

import DTO.SimpleResponse;
import db.ObjectMapper;
import spark.Request;
import spark.Response;

public interface HandlerController{

    public SimpleResponse<Object> mediate(Request requestObject, Response responseObject);
}
