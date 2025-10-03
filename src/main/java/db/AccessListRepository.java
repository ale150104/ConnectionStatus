package db;

import DTO.UserDTO;
import main.main;

import java.nio.file.FileAlreadyExistsException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Semaphore;

public class AccessListRepository {
    private static final String url = "jdbc:sqlite:%s".formatted(main.class.getResource("/db/sqlite/playground.sqlite"));

    private static AccessListRepository instance;

    private final Connection connection;

    private final Semaphore mutex;


    private AccessListRepository(Connection connection, Semaphore mutex)
    {
        this.connection = connection;
        this.mutex = mutex;
    }

    public static AccessListRepository getInstance() {
        if(instance == null)
        {
            try {
                instance = new AccessListRepository(DBConnection.getConnection(), DBConnection.getMutex());
            }
            catch(SQLException ex)
            {
                return null;
            }
        }
        return instance;
    }


    public boolean setVisibility(int IdUser1, int IdUser2)
    {
        String query = "INSERT INTO AccessList (user1, user2) values (%d, %d) ".formatted(IdUser1, IdUser2);
        String query2 ="INSERT INTO AccessList (user1, user2) values (%d, %d) ".formatted(IdUser2, IdUser1);

        try{
            this.mutex.acquire();
            Statement statement1 =  connection.createStatement();
            statement1.execute(query);

            Statement statement2 =  connection.createStatement();
            statement2.execute(query2);

            return (statement1.getUpdateCount() == 1 && statement2.getUpdateCount() == 1);
        }

        catch(InterruptedException ex)
        {
            return false;
        }
        catch(SQLException ex)
        {
            // SQLITE ERROR CODE FOR Unique Constraint failed -> Entry is already there
            if(ex.getErrorCode() == 19)
            {
                return true;
            }
            return false;
        }
        finally{
            this.mutex.release();
        }
    }


    public boolean deleteVisibility(int IdUser1, int IdUser2)
    {
        String query = "Delete from AccessList where user1=%d and user2=%d ".formatted(IdUser1, IdUser2);
        String query2 ="Delete from AccessList where user1=%d and user2=%d".formatted(IdUser2, IdUser1);

        try{
            this.mutex.acquire();
            Statement statement1 =  connection.createStatement();
            statement1.execute(query);

            Statement statement2 =  connection.createStatement();
            statement2.execute(query2);

            return (statement1.getUpdateCount() == 1 && statement2.getUpdateCount() == 1);
        }

        catch(InterruptedException ex)
        {
            return false;
        }
        catch(SQLException ex)
        {
            return false;
        }
        finally{
            this.mutex.release();
        }
    }
}
