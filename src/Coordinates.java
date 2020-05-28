import java.io.Serializable;

/**
        * @author Кирилл Сокоркин R3137
        * От данного класса создаются все Координаты, в которых хранятся Лабораторные Работы
        */
public class Coordinates implements Serializable {
    private double x; //Значение поля должно быть больше -722
    private float y;

    public Coordinates(double x, float y)
    {
        this.x = x;
        this.y = y;
    }
    /**
     * @return Координату X
     */
    public double getX() {
        return x;
    }
    /**
     * @return Координату Y
     */
    public float getY() {
        return y;
    }
}
