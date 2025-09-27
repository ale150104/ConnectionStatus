package DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class userLoginDTO{
    @JsonProperty
    public String userName;

    @JsonProperty
    public String password;
}
