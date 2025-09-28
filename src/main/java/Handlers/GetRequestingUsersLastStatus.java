package Handlers;

import DTO.SimpleResponse;
import DTO.StatusDTO;
import DTO.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.LoginOperations;
import db.StatusRepository;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.List;

public class GetRequestingUsersLastStatus implements Handler{
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


            List<StatusDTO> results;

            results = StatusRepository.getInstance().getStatusHistoryOfUser(user.Id());

            return new SimpleResponse<>(200, "Ok", results);
        }

        catch (SQLException ex)
        {
            responseObject.status(500);
            return new SimpleResponse<>(500, "SQL Error",null);

        }

    }

    @Override
    public String HandlableRoute() {
        return "/users/user/myStatus";
    }

    @Override
    public String HandlableMethod() {
        return WEB_GET_REQUEST;
    }


}
