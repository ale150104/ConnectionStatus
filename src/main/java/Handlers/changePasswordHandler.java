package Handlers;

import DTO.PasswordChangingDTO;
import DTO.SimpleResponse;
import DTO.User;
import DTO.UserDTO;
import Helper.HasPasswordAtLeast8CharsAnd4DifferentTypesOfChars;
import Helper.IIsPasswordSecure;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.LoginOperations;
import db.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;
import java.sql.SQLException;

public class changePasswordHandler implements Handler{
    @Override
    public SimpleResponse<Object> handle(Request requestObject, Response responseObject) {
        responseObject.type("application/json");
        ObjectMapper mapper = new ObjectMapper();

        try {
            UserDTO user = LoginOperations.getInstance().getUserFromSession(requestObject.headers("authorization"));


            if (user == null) {
                responseObject.status(401);

                return new SimpleResponse<>(401, "Unauthorized", null);
            }


            PasswordChangingDTO passwordChangeObject = mapper.readValue(requestObject.body(), PasswordChangingDTO.class);

            IIsPasswordSecure passwordChecker = new HasPasswordAtLeast8CharsAnd4DifferentTypesOfChars();
            if(!passwordChecker.isValid(passwordChangeObject.newPassword))
            {
                return new SimpleResponse<>(400, "Bad Request", "Password not matching. %s".formatted(passwordChecker.getPWGuideline()));
            }


            User userWithPassword = UserRepository.getInstance().getUser(user.userName());
            if(!BCrypt.checkpw(passwordChangeObject.oldPassword, userWithPassword.password))
            {
                return new SimpleResponse<>(400, "Bad Request", "Password wrong");
            }

            boolean result = UserRepository.getInstance().changePassword(userWithPassword, BCrypt.hashpw(passwordChangeObject.newPassword, BCrypt.gensalt(12)));

            if(!result)
            {
                return new SimpleResponse<>(500, "Internal Server Error", null);
            }

            LoginOperations.getInstance().logoutUser(user.Id());
            return new SimpleResponse<>(200, "Ok", "Additional Info, you have been logged out!");

        }
        catch(JsonProcessingException ex)
        {
            return new SimpleResponse<>(400, "Bad Request", "Body has wrong format");
        }
        catch(SQLException ex)
        {
            return new SimpleResponse<>(500, "Internal Server Error", null);
        }
    }

    @Override
    public String HandlableRoute() {
        return "/users/user/password";
    }

    @Override
    public String HandlableMethod() {
        return WEB_POST_REQUEST;
    }
}
