package Handlers;

import DTO.GeoAdress;
import DTO.SimpleResponse;
import DTO.StatusDTO;
import DTO.UserDTO;
import Helper.GeoLocation;
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


            StatusDTO result;

            result = StatusRepository.getInstance().getLastStatusOfUser(user.Id());


            try{
                GeoAdress adress = GeoLocation.getInstance().getLocation(result.status.length, result.status.width);

                result.status.prettyAdress = String.format("%S, %s, %s, %s %s", adress.countryCode, adress.city, adress.postalCode, adress.street, adress.houseNumber);
            }
            catch(Exception ex)
            {
                result.status.prettyAdress = "Not available";
            }


            return new SimpleResponse<>(200, "Ok", result);
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
