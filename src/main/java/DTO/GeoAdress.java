package DTO;

public class GeoAdress {

    public String city;

    public String countryCode;

    public String street;

    public String postalCode;

    public String houseNumber;

    public GeoAdress(String _city, String _countryCode, String _street, String _postalCode, String _houseNumber)
    {
        this.city = _city;
        this.countryCode = _countryCode;
        this.street = _street;
        this.postalCode = _postalCode;
        this.houseNumber = _houseNumber;
    }
}
