package DTO;

import db.RowMapper;

import java.sql.ResultSet;

public class UserMapperFromDB implements RowMapper<User> {

    @Override
    public User map(ResultSet set) throws Exception {

        return new User(
                set.getInt("Id"),
                set.getString("Name"),
                set.getString("LastName"),
                set.getString("userName"),
                set.getString("Password"),
                set.getBoolean("isAdmin")
                );
    }
}
