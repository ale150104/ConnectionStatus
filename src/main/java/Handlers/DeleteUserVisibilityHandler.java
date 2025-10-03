package Handlers;

import DTO.SimpleResponse;
import DTO.User;
import DTO.UserDTO;
import DTO.UserVisibilityDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.AccessListRepository;
import db.LoginOperations;
import db.UserRepository;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

public class DeleteUserVisibilityHandler implements Handler{
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

            UserVisibilityDTO userVisibilty = mapper.readValue(requestObject.body(), UserVisibilityDTO.class);

            User user1 = UserRepository.getInstance().getUser(userVisibilty.UserId1);
            User user2 = UserRepository.getInstance().getUser(userVisibilty.UserId2);

            if(user1 == null || user2 == null)
            {
                return new SimpleResponse<>(400, "Bad Request", "User not present in DB");
            }


            boolean result = AccessListRepository.getInstance().deleteVisibility(userVisibilty.UserId1, userVisibilty.UserId2);

            if(!result)
            {
                return new SimpleResponse<>(500, "Internal Server Error", null);
            }
            return new SimpleResponse<>(200, "Ok", null);

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
        return "/users/visibility";
    }

    @Override
    public String HandlableMethod() {
        return WEB_DELETE_REQUEST;
    }
}
