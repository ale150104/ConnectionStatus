package main;

import DTO.*;

import Handlers.Mediator;
import com.fasterxml.jackson.databind.*;
import spark.Route;
import java.sql.*;
import static spark.Spark.*;


public class main {

    public static void main(String[] args) throws SQLException {
        Route func =  (request, response) -> {
            Mediator mediator =  new Mediator();

            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(mediator.mediate(request, response));
            }
            catch(Exception ex)
            {
                return null;
            }
        };


        get("/users/status", func);

        post("/users/user/myStatus", func);

        post("/users/user/login", func);
    }
}
