package Helper;

import DTO.GeoAdress;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.security.EcPrivateJwk;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class GeoLocation {

    private static GeoLocation instance;

    private final String apiKey = "68dbd4c9a224e483116708zswd3197c";

    private final int waitTimeAfterRequestSeconds = 1;

    private LocalDateTime lastRequest = LocalDateTime.of(1971, 1 ,1, 0, 0);

    private GeoLocation(){}

    public static GeoLocation getInstance()
    {
        if(instance == null)
        {
            instance = new GeoLocation();
        }
        return instance;
    }

    public GeoAdress getLocation(double longitude, double latitude) throws Exception
    {
        if(this.lastRequest.until(LocalDateTime.now(), ChronoUnit.SECONDS) < this.waitTimeAfterRequestSeconds)
        {
            throw new HttpTimeoutException("RequestCycle too short");
        }

        Locale.setDefault(Locale.ENGLISH);
        String url = "https://geocode.maps.co/reverse?lat=%f&lon=%f&api_key=%s".formatted(latitude, longitude, apiKey);

        Locale.setDefault(Locale.GERMAN);
        try(HttpClient client = HttpClient.newBuilder().build())
        {

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());

            String country = node.get("address").get("country_code").textValue();
            String city;
            try{
                city = node.get("address").get("town").textValue();
            }
            catch(Exception ex)
            {
                try{
                    city = node.get("address").get("village").textValue();
                }
                catch(Exception x)
                {
                    city = "{ City Not available }";
                }
            }


            String street = node.get("address").get("road").textValue();
            String postalCode = node.get("address").get("postcode").textValue();
            String housenumber;
            try{
               housenumber = node.get("address").get("house_number").textValue();
            }
            catch(Exception x)
            {
                housenumber = "";
            }
            this.lastRequest = LocalDateTime.now();

            return new GeoAdress(city, country, street, postalCode, housenumber);
        }
        catch(URISyntaxException | IOException | InterruptedException | NullPointerException ex)
        {

            throw new Exception("Something went wrong");
        }


    }
}
