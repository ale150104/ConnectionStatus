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

public class SetUserStatusHandler implements Handler{

    @Override
    public SimpleResponse handle(Request requestObject, Response responseObject) {
        SimpleResponse<String> responseBody;

        responseObject.type("application/json");
        ObjectMapper mapper = new ObjectMapper();

        UserDTO user;
        try {
            user = LoginOperations.getInstance().getUserFromSession(requestObject.headers("authorization"));

            if (user == null) {
                responseBody = new SimpleResponse<>(401, "Unauthorized", null);
                responseObject.status(401);
                return responseBody;
            }

            Boolean result = false;

            Status newStatus = mapper.readValue(requestObject.body(), Status.class);
            newStatus.timestamp = LocalDateTime.now();
            result = StatusRepository.getInstance().setStatus(newStatus, user);

            responseBody = new SimpleResponse<>(200, "Ok", mapper.writeValueAsString(newStatus));
            responseObject.status(200);
            return responseBody;

        }

        catch(SQLException | JsonProcessingException ex)
        {
            responseBody = new SimpleResponse<>(500, "Processing Error", null);
            responseObject.status(500);
            return responseBody;
        }

    }



    @Override
    public String HandlableRoute() {
        return "/users/user/myStatus";
    }
}
