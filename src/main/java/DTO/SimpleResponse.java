package DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SimpleResponse <T>(


        @JsonProperty
        Integer status,

        @JsonProperty
        String text,

        @JsonProperty
        T data
) {
}