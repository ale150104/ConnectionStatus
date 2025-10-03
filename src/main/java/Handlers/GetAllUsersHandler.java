package Handlers;

import DTO.SimpleResponse;
import DTO.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.LoginOperations;
import db.UserRepository;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.List;

public class GetAllUsersHandler implements Handler{
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

            if (!user.isAdmin())
            {
                return new SimpleResponse<>(403, "Forbidden", "No Permission for this operation");
            }

            List<UserDTO> userResults = UserRepository.getInstance().getAllUsers();
            return new SimpleResponse<>(200, "Ok", userResults);

        }
        catch(SQLException | NullPointerException ex)
        {
            return new SimpleResponse<>(500, "Internal Server Error", null);
        }
    }

    @Override
    public String HandlableRoute() {
        return "/users";
    }

    @Override
    public String HandlableMethod() {
        return WEB_GET_REQUEST;
    }
}
