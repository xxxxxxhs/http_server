package CollectionClasses;

import Exceptions.IncorrectValueException;

import java.io.Serializable;

/*
 * class location
 */

public class Location implements Serializable, Validatable {
    
    private Double x; //Поле не может быть null
    private Float y; //Поле не может быть null
    private Long z; //Поле не может быть null
    private String name; //Поле не может быть null
    public static final long SerialVersionUID = 123456;

    public Location(Double x, Float y, Long z, String name) throws IncorrectValueException{
        setX(x);
        setY(y);
        setZ(z);
        setName(name);
    }
    public Double getX(){
        return x;
    }
    public Float getY(){
        return y;
    }
    public Long getZ(){
        return z;
    }
    public String getName(){
        return name;
    }
    public void setX(Double x) {
        this.x = x;
    }
    public void setY(Float y) {
        this.y = y;
    }
    public void setZ(Long z) {
        this.z = z;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean validate() {
        if (this.name != null && this.x != null && this.y != null && this.z != null) {return true;}
        else {return false;}
    }
    @Override
    public String toString(){
        return name + " (" + x + ", " + y + ", " + z + ")";
    }
}

