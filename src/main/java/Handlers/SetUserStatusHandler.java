package Handlers;

import DTO.SimpleResponse;
import DTO.Status;
import DTO.StatusDTO;
import DTO.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.LoginOperations;
import db.StatusRepository;
import db.UserRepository;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class SetUserStatusHandler implements Handler{

    @Override
    public SimpleResponse handle(Request requestObject, Response responseObject) {

        responseObject.type("application/json");
        ObjectMapper mapper = new ObjectMapper();

        UserDTO user;
        try {
            user = LoginOperations.getInstance().getUserFromSession(requestObject.headers("authorization"));

            if (user == null) {
                responseObject.status(401);
                return new SimpleResponse<>(401, "Unauthorized", null);
            }

            Boolean result = false;

            Status newStatus = mapper.readValue(requestObject.body(), Status.class);
            if(!newStatus.isValid())
            {
                responseObject.status(400);
                return new SimpleResponse<String>(400, "Bad Request", "Input data is not in allowed ranges");
            }
            newStatus.SetTimeStamp(LocalDateTime.now());

            result = StatusRepository.getInstance().setStatus(newStatus, user);
            if(!result)
            {
                responseObject.status(500);
                return new SimpleResponse<Status>(500, "SQL Exception", newStatus);
            }

            responseObject.status(200);

            return new SimpleResponse<Status>(200, "Ok", newStatus);

        }

        catch(SQLException | JsonProcessingException ex)
        {
            responseObject.status(500);
            return new SimpleResponse<>(500, "Processing Error", null);
        }

    }



    @Override
    public String HandlableRoute() {
        return "/users/user/myStatus";
    }

    @Override
    public String HandlableMethod() {
        return WEB_POST_REQUEST;
    }
}
