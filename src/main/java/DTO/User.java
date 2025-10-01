package DTO;

public class User {
    public int Id;

    public String name;

    public String Lastname;

    public String eMail;

    public String password;

    public boolean isAdmin;


    public User(int _Id, String _name, String _Lastname, String _eMail, String _password, boolean _isAdmin)
    {
        this.Id = _Id;
        this.name = _name;
        this.Lastname = _Lastname;
        this.eMail = _eMail;
        this.password = _password;
        this.isAdmin = _isAdmin;
    }
}