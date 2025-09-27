package main;

import DTO.UserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class test {

    @JsonProperty("status")
    public String status = "OK";

    @JsonProperty("data")
    public List<UserDTO> user = List.of(new UserDTO(1, "Penis", "Penis", "Penis", false),
    new UserDTO(2, "Penis", "Penis", "Penis", false));

}
