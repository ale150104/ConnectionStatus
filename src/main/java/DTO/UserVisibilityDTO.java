package DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserVisibilityDTO {

    @JsonProperty(required = true)
    public int UserId1;

    @JsonProperty(required = true)
    public int UserId2;
}
