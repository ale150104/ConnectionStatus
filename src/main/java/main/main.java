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
                return mapper.writeValueAsString(mediator.mediate(request, response));
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
