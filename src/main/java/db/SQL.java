package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class SQL implements  AutoCloseable {

    private Connection connection;

    private SQL(String url) {}


    private static SQL instance;

    //SINGLETON
    public static SQL getInstance(Optional<String> url) throws Exception {

        if(instance == null)
        {
            if(url.isEmpty())
            {
                throw new Exception();
            }
            instance = new SQL(url.get());
        }

        return instance;
    }


    public <T> ArrayList<T> query(String sql, RowMapper<T> mapper) throws Exception {

        ResultSet resultSet = this.connection.createStatement().executeQuery(sql);

        ArrayList<T> objects = new ArrayList<>();

        while(resultSet.next())
        {
            objects.add(mapper.map(resultSet));
        }

        return  objects;
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }

    public <T> boolean update(T object, ObjectMapper<T> mapper) throws Exception {

        return this.connection.createStatement().execute(String.format("UPDATE XXX %s on TABLE",mapper.map(object)));
    }

    public <T> boolean create(T object, ObjectMapper<T> mapper) throws Exception {

        return this.connection.createStatement().execute(String.format("CREATE XXX %s on TABLE",mapper.map(object)));
    }

}
