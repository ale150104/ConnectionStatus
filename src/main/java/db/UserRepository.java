package db;

import DTO.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.main;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.file.FileAlreadyExistsException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.Semaphore;

public class UserRepository {

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


    public User getSingleDataSet(Integer identifier) {
        return null;
    }

    public LinkedList<User> getAllDataSets() {
        return null;
    }

    public UserDTO add(User dataSet) throws FileAlreadyExistsException {

    String hashedPassword = BCrypt.hashpw(dataSet.password, BCrypt.gensalt(12));
    String query = "INSERT INTO Users (Name, LastName, isAdmin, Password, userName) values ('%s', '%s', %d, '%s', '%s')".formatted(dataSet.name, dataSet.Lastname, (dataSet.isAdmin)? 1 : 0, hashedPassword, dataSet.userName);
    boolean result;
    try{
        this.mutex.acquire();
        result =  connection.createStatement().execute(query);
    }

    catch(InterruptedException ex)
    {
        return null;
    }
    catch(SQLException ex)
    {
        throw new FileAlreadyExistsException("");
    }
    finally{
        this.mutex.release();
    }

    return UserDTO.from(this.getUser(dataSet.userName));

    }

    public User getUser(String userName)
    {
        String query = "SELECT * FROM Users where Users.userName = '%s'".formatted(userName);
        RowMapper<User> mapper = new UserMapperFromDB();
        try{
            this.mutex.acquire();
            ResultSet set = connection.createStatement().executeQuery(query);
            if(set.next())
            {
                return mapper.map(set);
            }

            return null;
        }
        catch(Exception ex)
        {
            return null;
        }
        finally{
            this.mutex.release();
        }
    }


    public boolean changePassword(User user, String newPW)
    {
        String query = "update Users set password = '%s' where Users.Id = %d".formatted(newPW, user.Id);
        try{
            this.mutex.acquire();
            boolean result  = connection.createStatement().execute(query);
            return result;
        }
        catch(Exception ex)
        {
            return false;
        }
        finally{
            this.mutex.release();
        }
    }

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
