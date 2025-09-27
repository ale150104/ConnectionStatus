package DTO;

public record User(
   int Id,
   String name,
   String Lastname,
   String eMail,
   String password,
   boolean isAdmin
){}

