package main;

import DTO.*;

import Handlers.Mediator;
import com.fasterxml.jackson.databind.*;
import db.DBConnection;
import spark.Route;
import java.sql.*;
import static spark.Spark.*;


public class main {

    public static void main(String[] args) throws SQLException {
        Route func =  (request, response) -> {
            Mediator mediator =  new Mediator();

            ObjectMapper mapper = new ObjectMapper();
            try {
                SimpleResponse<Object> handlerResponse = mediator.mediate(request, response);
                response.status(handlerResponse.status());
                return mapper.writeValueAsString(handlerResponse);
            }
            catch(Exception ex)
            {
                return null;
            }
        };


        get("/users/status", func);

        post("/users/user/myStatus", func);

        get("/users/user/myStatus", func);

        post("/users/user/login", func);

        post("/users/user/new", func);

        post("/users/user/password", func);

        delete("/users/user", func);

        get("/users", func);

        post("/users/visibility", func);

        delete("/users/visibility", func);



        // DOC: Reference System for Geolocation: Just longitude and latitude referring to WGS84

        // NICE HAVE TODO: Admin Funktionen

        // TODO: Endpunkt zur Textuellen Anzeige von Koordinaten (Standort) --> DONE

        // TODO: Get my Last status --> DONE

        //TODO: Richtige Darstellung der Koordinaten--> DONE



        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("Closing");
                            DBConnection.getConnection().commit();
                            DBConnection.getConnection().close();
                        }
                        catch (SQLException e) {}
                    }
                })
        );
    }
}
