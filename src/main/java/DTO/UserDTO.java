package DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDTO (
        @JsonProperty("Id")
        int Id,

        @JsonProperty("name")
        String name,

        @JsonProperty("lastName")
        String lastName,
        @JsonProperty("userName")
        String userName,

        @JsonProperty("isAdmin")
        boolean isAdmin
){
    public static UserDTO from(User user){
        return new UserDTO(user.Id(), user.name(), user.Lastname(), user.eMail(), user.isAdmin());
    }

}
