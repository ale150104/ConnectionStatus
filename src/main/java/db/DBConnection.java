package db;

import main.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

public class DBConnection implements AutoCloseable{
    private static final String url = "jdbc:sqlite:%s".formatted(main.class.getResource("/db/sqlite/playground.sqlite"));

    private static Connection connection;

    private static Semaphore mutex = new Semaphore(1);

    public static  Connection getConnection() throws SQLException {
        if(connection == null)
        {
            connection = DriverManager.getConnection(url);
        }

        return connection;
    }

    public static Semaphore getMutex()
    {
        return mutex;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}

