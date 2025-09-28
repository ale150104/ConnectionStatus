package DTO;

import db.RowMapper;

import java.sql.ResultSet;
import java.time.LocalDateTime;

public class StatusMapperFromDB implements RowMapper<StatusDTO> {
    @Override
    public StatusDTO map(ResultSet set) throws Exception {

        UserDTO user = new UserDTO(
                set.getInt("Id"),
                set.getString("Name"),
                set.getString("LastName"),
                set.getString("userName"),
                false
        );

        Status status = Status.fromDB(
                set.getDouble("breitenGrad"),
                set.getDouble("laengenGrad"),
                LocalDateTime.parse(set.getString("timeStamp").replace(" ", "T")),
                (short) set.getInt("battery")

        );

        StatusDTO returnObj = new StatusDTO();
        returnObj.User = user;
        returnObj.status = status;

        return returnObj;

    }
}
