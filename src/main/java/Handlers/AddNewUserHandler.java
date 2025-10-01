package Handlers;

import DTO.*;
import Helper.GeoLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.LoginOperations;
import db.StatusRepository;
import db.UserRepository;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

public class AddNewUserHandler implements Handler{
    @Override
    public SimpleResponse<Object> handle(Request requestObject, Response responseObject) {
        responseObject.type("application/json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            UserDTO user = LoginOperations.getInstance().getUserFromSession(requestObject.headers("authorization"));

            if (user == null) {
                responseObject.status(401);

                return new SimpleResponse<>(401, "Unauthorized", null);
            }

            if(!user.isAdmin())
            {
                return new SimpleResponse<>(403,"Forbidden", "No Permission for this operation");
            }


            User usr = mapper.readValue(requestObject.body(), User.class);
            boolean result = UserRepository.getInstance().add(usr);

            if(!result)
            {
                return new SimpleResponse<>(500, "Internal Server Error", null);
            }
            return new SimpleResponse<>(200, "Ok", usr);

        }
        catch(JsonProcessingException ex)
        {
            return new SimpleResponse<>(400, "Bad Request", "Body has wrong format");
        }
        catch(SQLException ex)
        {
            return new SimpleResponse<>(500, "Internal Server Error", null);
        }
    }

    @Override
    public String HandlableRoute() {
        return "/users/user/new";
    }

    @Override
    public String HandlableMethod() {
        return WEB_POST_REQUEST;
    }
}
