package Handlers;

import DTO.SimpleResponse;
import DTO.Status;
import DTO.StatusDTO;
import DTO.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.LoginOperations;
import db.StatusRepository;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class GetUserStatiHandler implements Handler{
    @Override
    public SimpleResponse<Object> handle(Request requestObject, Response responseObject) {
        responseObject.type("application/json");
        ObjectMapper mapper = new ObjectMapper();

        SimpleResponse responseBody;

        try {
            UserDTO user = LoginOperations.getInstance().getUserFromSession(requestObject.headers("authorization"));

            if (user == null) {
                responseObject.status(401);

                responseBody = new SimpleResponse<>(401, "Unauthorized", null);
            }


            List<StatusDTO> results;

            results = StatusRepository.getInstance().getStatusOfUsers(user.Id());
            responseBody = new SimpleResponse<List<StatusDTO>>(200, "Ok", results);
        }

        catch (SQLException ex)
        {
            responseBody = new SimpleResponse<>(500, "SQL Error",null);
            responseObject.status(500);

        }
        catch (Exception ex)
        {
            responseBody = new SimpleResponse<>(500, "Internal Server Error",null);
            responseObject.status(500);
        }

        return responseBody;
    }

    @Override
    public String HandlableRoute() {
        return "/users/status";
    }

    @Override
    public String HandlableMethod() {
        return WEB_GET_REQUEST;
    }

}
