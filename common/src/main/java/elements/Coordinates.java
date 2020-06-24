package elements;


import java.io.Serializable;

public class Coordinates implements Serializable {
    private Double x; //Поле не может быть null
    private Long y; //Значение поля должно быть больше -109, Поле не может быть null
        public Coordinates(double x, long y){
            this.x=x;
            this.y=y;
        }

    public Double getX() {
        return x;
    }

    public Long getY() {
        return y;
    }

    public String toString() {
          return "x = " + x + "\n"+
                 "y = " + y;
        }
}
