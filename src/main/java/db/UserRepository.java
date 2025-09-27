package db;

import DTO.*;
import main.main;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.Semaphore;

public class UserRepository implements Repository<User, Integer> {

    private static final String url = "jdbc:sqlite:%s".formatted(main.class.getResource("/db/sqlite/playground.sqlite"));

    private static UserRepository instance;

    private final Connection connection;

    private final Semaphore mutex;


    private UserRepository(Connection connection, Semaphore mutex)
    {
        this.connection = connection;
        this.mutex = mutex;
    }

    public static UserRepository getInstance() {
        if(instance == null)
        {
            try {
                instance = new UserRepository(DBConnection.getConnection(), DBConnection.getMutex());
            }
            catch(SQLException ex)
            {
                return null;
            }
        }

        return instance;

    }


    @Override
    public User getSingleDataSet(Integer identifier) {
        return null;
    }

    @Override
    public LinkedList<User> getAllDataSets() {
        return null;
    }

    @Override
    public boolean add(User dataSet) {

    String hashedPassword = BCrypt.hashpw(dataSet.password(), BCrypt.gensalt(12));
    String query = "INSERT INTO Users (Name, LastName, isAdmin, Password, userName) values (%s, %s, %d, %s, %s)".formatted(dataSet.name(), dataSet.Lastname(), (dataSet.isAdmin())? 1 : 0, hashedPassword, dataSet.eMail());
    boolean result;
    try{
        this.mutex.acquire();
        result =  connection.createStatement().execute(query);
    }
    catch(SQLException | InterruptedException ex)
    {
        result = false;
    }
    finally{
        this.mutex.release();
    }

    return result;

    }

    @Override
    public boolean delete(Integer identifier) {

        String query1 = "DELETE from AccessList where AccessList.user2 = %1$d".formatted(identifier);
        String query2 = "DELETE from DTO.Status where DTO.Status.UserId = %1$d".formatted(identifier);
        String query3 = "DELETE from Users where Users.Id = %1$d".formatted(identifier);

        boolean res1 = false;
        boolean res2 = false;
        boolean res3 = false;

        try{
            this.mutex.acquire();
             res1 = connection.createStatement().execute(query1);
             res2 = connection.createStatement().execute(query2);
             res3 = connection.createStatement().execute(query3);
        }
        catch(SQLException | InterruptedException ex )
        {}
        finally{
            this.mutex.release();
        }

        return !(res1 | res2 | res3);
    }

}
