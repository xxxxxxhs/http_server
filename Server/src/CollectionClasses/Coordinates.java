package CollectionClasses;

import Exceptions.IncorrectValueException;

import java.io.Serializable;

/*
 * class coordinates
 */

public class Coordinates implements Serializable, Validatable {
    
    private float x; //Значение поля должно быть больше -170
    private double y;
    public static final long SerialVersionUID = 1234288;
    
    public Coordinates(float x, double y) {
        setX(x);
        setY(y);
    }
    public void setX(float x){
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }
    public float getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    @Override
    public boolean validate() {
        if (this.x <= -170) return false;
        return true;
    }
    @Override
    public String toString(){
        return "(x == " + x + ", y == " + y + ")";
    }
}
