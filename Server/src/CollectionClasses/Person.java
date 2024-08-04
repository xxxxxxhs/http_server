package CollectionClasses;

/*
 * class Person
 * describes movie's screenwriter
 */

import java.io.Serializable;

public class Person implements Serializable, Validatable {
    private long id;
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String passportID; //Поле не может быть null
    private Color eyeColor; //Поле может быть null
    private Color hairColor; //Поле не может быть null
    private Location location; //Поле может быть null
    public static final long SerialVersionUID = 123488;
    
    public Person (String name, String passportID, Color eyeColor, Color hairColor, Location locantion) {
        setName(name);
        setPassportID(passportID);
        setEyeColor(hairColor);
        setHairColor(hairColor);
        setLocation(locantion);
    }
    public String getName(){
        return name;
    }
    public String getPassportId(){
        return passportID;
    }
    public Color getEyeColor(){
        return eyeColor;
    }
    public Color getHairColor(){
        return hairColor;
    }
    public Location getLocation(){
        return location;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }
    public void setEyeColor(Color color) {
        this.eyeColor = color;
    }
    public void setHairColor(Color color) {
        this.hairColor = color;
    }
    public void setLocation(Location location){
        this.location = location;
    }
    public void setId(long id) {this.id = id;}
    @Override
    public String toString(){
        return name + "\n";
    }

    @Override
    public boolean validate() {
        if (this.name == null || this.name.equals("")) {return false;}
        if (this.passportID == null) {return false;}
        if (this.hairColor == null) {return  false;}
        if (!this.location.validate()) {return false;}
        return true;
    }
}

