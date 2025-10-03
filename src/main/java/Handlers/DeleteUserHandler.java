package Handlers;

import DTO.SimpleResponse;
import DTO.User;
import DTO.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.LoginOperations;
import db.UserRepository;
import spark.Request;
import spark.Response;
import java.sql.SQLException;

public class DeleteUserHandler implements Handler{
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
            if(usr.userName.isEmpty())
            {
                return new SimpleResponse<>(400, "Bad Request", "No username");
            }

            UserDTO result;
            User fullUserObject = UserRepository.getInstance().getUser(usr.Id);

            if(fullUserObject == null || !fullUserObject.userName.equals(usr.userName))
            {
                return new SimpleResponse<>(404, "Not found", null);
            }
            try{
                result = UserRepository.getInstance().delete(usr.Id);
            }
            catch(NullPointerException ex)
            {
                return new SimpleResponse<>(500, "Internal Server Error", null);
            }

            if(result == null)
            {
                return new SimpleResponse<>(500, "Internal Server Error", null);
            }
            return new SimpleResponse<>(200, "Ok", result);

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
        return "/users/user";
    }

    @Override
    public String HandlableMethod() {
        return WEB_DELETE_REQUEST;
    }
}
