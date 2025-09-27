package DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Status{
    @JsonProperty
    public double width;

    @JsonProperty
    public double length;

    @JsonIgnore
    public LocalDateTime timestamp;

    @JsonProperty
    public short battery;


    public Status(){}
    public boolean isValid()
    {
        return this.length >= -180 && this.length <= 180 && this.width >= - 90 && this.width <= 90 && this.battery > 0 && this.battery <= 100;
    }

    private Status (double width, double length, LocalDateTime timeStamp, short battery)
    {
        this.width = width;
        this.length = length;
        this.battery = battery;
        this.timestamp = timeStamp;
    }

    static Status fromDB(double width, double length, LocalDateTime timeStamp,  short battery)
    {
        return new Status(width, length, LocalDateTime.now(), battery);
    }



    static Status forDB(double width, double length,  short battery)
    {
        return new Status(width, length, LocalDateTime.now(), battery);
    }

}
