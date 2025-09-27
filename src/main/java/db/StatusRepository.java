package db;

import DTO.Status;
import DTO.StatusDTO;
import DTO.StatusMapperFromDB;
import DTO.UserDTO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Semaphore;

public class StatusRepository {

    private final Connection connection;

    private static StatusRepository instance;

    private final Semaphore mutex;

    private StatusRepository(Connection connection, Semaphore mutex) {
        this.connection = connection;
        this.mutex = mutex;
    }

    public static StatusRepository getInstance()
    {
        if(instance == null)
        {
            try {
                instance = new StatusRepository(DBConnection.getConnection(), DBConnection.getMutex());
            }
            catch(SQLException ex)
            {
                return null;
            }
        }

        return instance;
    }

    public ArrayList<StatusDTO> getStatusOfUsers(Integer forUser) throws SQLException {
    String query =
            "SELECT Users.Id, Users.Name,  Users.LastName, Users.userName, max(Status.timeStamp) as 'timeStamp', Status.laengenGrad, Status.breitenGrad, Status.battery from (Users JOIN AccessList on AccessList.user1 = %d and AccessList.user2 = Users.Id) JOIN Status on Status.UserId = Users.Id GROUP By Users.Id".formatted(forUser);

    try {
        this.mutex.acquire();
        ResultSet set = connection.createStatement().executeQuery(query);
        RowMapper<StatusDTO> mapper = new StatusMapperFromDB();

        ArrayList<StatusDTO> results = new ArrayList<>();
        while (set.next()) {

            results.add(mapper.map(set));
        }

        return results;

    }

    catch(Exception ex)
    {
        return null;
    }
    finally {
        this.mutex.release();
    }

    }


    public boolean setStatus(Status status, UserDTO forUser) throws SQLException {
        Locale.setDefault(Locale.ENGLISH);


        String dateTimeForSQLITE = status.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String query = "INSERT INTO Status (timeStamp, UserId, laengenGrad, breitenGrad, battery) values('%s', %d, %f, %f, %d)".formatted(dateTimeForSQLITE , forUser.Id(), status.length, status.width, status.battery);
        Locale.setDefault(Locale.GERMAN);

        try {
            this.mutex.acquire();
            Statement statement = connection.createStatement();
            statement.execute(query);
            return statement.getUpdateCount() == 1;
        }
        catch( Exception ex)
        {
            return false;
        }
        finally
        {
            this.mutex.release();
        }
    }
}
