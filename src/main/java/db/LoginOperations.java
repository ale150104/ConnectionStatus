package db;

import DTO.User;
import DTO.UserDTO;
import DTO.UserMapperFromDB;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import main.main;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.sql.*;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class LoginOperations
{
    private SecretKey privateKey;

    private  final String privateKeyOutputUri = "/jwt/privateKey/pvk.bin";

    private static LoginOperations instance;

    private final Connection connection;

    private final Semaphore mutex;



    private LoginOperations(Connection connection, Semaphore mutex)
    {
        this.connection = connection;
        this.mutex = mutex;


        char[] pwdArray = "password".toCharArray();

        Path pathToSecretKey = Path.of("C:/temp/jwt/Key/SecretKey.jks");

        try{
            System.out.println(pathToSecretKey);
        }
        catch(NullPointerException ex)
        {
            System.out.println("FUCK");
        }

        try (FileInputStream is = new FileInputStream(pathToSecretKey.toFile())){

            KeyStore ks = KeyStore.getInstance("pkcs12");
            ks.load(is, pwdArray);

            KeyStore.SecretKeyEntry wrapper = (KeyStore.SecretKeyEntry)  ks.getEntry("Status", new KeyStore.PasswordProtection(pwdArray));
            privateKey = wrapper.getSecretKey();

        }
        catch ( KeyStoreException | UnrecoverableEntryException | IOException | NoSuchAlgorithmException | CertificateException e)

        {
            try (FileOutputStream ous = new FileOutputStream(pathToSecretKey.toFile())){

                KeyStore ks = KeyStore.getInstance("pkcs12");

                ks.load(null, pwdArray);

                privateKey = Jwts.SIG.HS512.key().build();

                KeyStore.SecretKeyEntry privateKeyWrapper = new KeyStore.SecretKeyEntry(privateKey);

                KeyStore.PasswordProtection password = new KeyStore.PasswordProtection(pwdArray);

                ks.setEntry("Status", privateKeyWrapper, password);

                ks.store(ous, pwdArray);
            }
            catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException ex) {
                System.out.println("Doppel Fuck");
                System.out.println(ex.getMessage());
                System.exit(101);
            }
        }
    };

    public static LoginOperations getInstance()
    {
        if(instance == null)
        {
            try {
                instance = new LoginOperations(DBConnection.getConnection(), DBConnection.getMutex());
            }
            catch(SQLException ex)
            {
                return null;
            }
        }

        return instance;
    }



    public String generateToken(String userName, String password) throws SQLException {
        User user = userInDB(userName);

        if (user == null){
            return null;
        }

        if(!BCrypt.checkpw(password, user.password()))
        {
            return null;
        }

        String token = Jwts
                .builder()
                .subject(user.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)))
                .signWith(privateKey)
                .compact();

        System.out.println("INSERT INTO Login VALUES('%1$s', %2$d) ON CONFLICT(UserId) DO UPDATE SET token = '%1$s'".formatted(token, user.Id()));
        int success;
        try{
            this.mutex.acquire();
            success = connection.createStatement().executeUpdate("INSERT INTO Login VALUES('%1$s', %2$d) ON CONFLICT(UserId) DO UPDATE SET token = '%1$s'".formatted(token, user.Id()));
            return (success == 1)? token : null;
        }

        catch(InterruptedException ex)
        {
            return null;
        }

        finally {
            this.mutex.release();
        }

    }


    private User userInDB(String userName) throws SQLException{
        try{
            ResultSet set;
            this.mutex.acquire();
             set = this.connection.createStatement().executeQuery("SELECT * from Users where Users.userName = '%s'".formatted(userName));

             if(!set.next())
            {
                return null;
            }

            RowMapper<User> mapper = new UserMapperFromDB();
            try{
                return mapper.map(set);

            }
            catch (Exception ex)
            {
                throw new SQLException(ex.getMessage());
            }

        }
        catch(InterruptedException ex)
        {
            return null;
        }
        finally {
            this.mutex.release();
        }

    }


    public  UserDTO getUserFromSession(String bearer) throws SQLException {

        try {
            this.mutex.acquire();
            ResultSet set = this.connection.createStatement().executeQuery(("SELECT * from Users inner join Login on Users.Id = Login.UserId where Login.token = '%s'").formatted(bearer));

            if (!set.next()) {
                return null;
            }

            //String decode and verification
            Jwts.parser()
                    .verifyWith(privateKey)
                    .build()
                    .parseSignedClaims(set.getString("token"));


            RowMapper<User> mapper = new UserMapperFromDB();
            try {
                return UserDTO.from(mapper.map(set));

            } catch (JwtException e) {
                return null;
            } catch (Exception ex) {
                throw new SQLException(ex.getMessage());
            }

        }
        catch(InterruptedException ex)
        {
            return null;
        }
        finally {
            this.mutex.release();
        }
    }




}
