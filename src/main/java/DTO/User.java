package DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    public int Id;

    public String name;

    public String Lastname;

    public String userName;

    public String password;

    public boolean isAdmin;


    public User(){}

    public User(int _Id, String _name, String _Lastname, String _username, String _password, boolean _isAdmin)
    {
        this.Id = _Id;
        this.name = _name;
        this.Lastname = _Lastname;
        this.userName = _username;
        this.password = _password;
        this.isAdmin = _isAdmin;
    }
}