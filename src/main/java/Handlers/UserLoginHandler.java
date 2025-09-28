package Handlers;

import DTO.SimpleResponse;
import DTO.userLoginDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.LoginOperations;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

public class UserLoginHandler implements Handler{

    @Override
    public SimpleResponse handle(Request requestObject, Response responseObject) {

        SimpleResponse<String> responseBody;

        responseObject.type("application/json");
        ObjectMapper mapper = new ObjectMapper();

        userLoginDTO userData;

        try{
         userData = mapper.readValue(requestObject.body(), userLoginDTO.class);

        if(userData == null)
        {
            responseBody = new SimpleResponse<>(401, "Unauthorized",null);
            responseObject.status(401);
            return responseBody;
        }

        }
        catch(JsonProcessingException ex)
        {
            responseBody = new SimpleResponse<>(500, "Processing Error", null);
            responseObject.status(500);
            return responseBody;
        }




        //TODO: PW Hashing

    String token;
        try {
             token = LoginOperations.getInstance().generateToken(userData.userName, userData.password);
            if(token == null)
            {
                responseBody = new SimpleResponse<>(404, "Not Found",null);
                responseObject.status(404);
                responseObject.body(mapper.writeValueAsString(responseBody));
            }

            System.out.println("Logged in");
            responseBody = new SimpleResponse<>(200, "Ok", token);
        }
        catch (JsonProcessingException e) {
            responseBody = new SimpleResponse<>(500, "Processing Exception",null);
            responseObject.status(500);
        }

        catch(SQLException ex){
            responseBody = new SimpleResponse<>(500, "SQL Error",null);
            responseObject.status(500);
        }

        return responseBody;

    }


    @Override
    public String HandlableRoute() {
        return "/users/user/login";
    }

    @Override
    public String HandlableMethod() {
        return WEB_POST_REQUEST;
    }
}
