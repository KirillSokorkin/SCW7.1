import java.time.LocalDate;
/**
 * @author Кирилл Сокоркин R3137
 * От данного класса создаются все Лабораторные Работы
 */
public class LabWork implements Comparable<LabWork>{
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long minimalPoint; //Поле может быть null, Значение поля должно быть больше 0
    private Long personalQualitiesMinimum; //Поле может быть null, Значение поля должно быть больше 0
    private String description; //Поле не может быть null
    private Difficulty difficulty; //Поле может быть null

    public LabWork(String name, Coordinates coordinates, Long minimalPoint, Long personalQualitiesMinimum, String description,
                   Difficulty difficulty){

        this.name = name;
        this.coordinates = coordinates;
        this.minimalPoint = minimalPoint;
        this.personalQualitiesMinimum = personalQualitiesMinimum;
        this.description = description;
        this.difficulty = difficulty;

        this.id = this.hashCode();
        this.creationDate = LocalDate.now();
    }
    /**
     * @param o Объект класса LabWork
     * @return 1, если ID больше и -1, если ID меньше
     */
    @Override
    public int compareTo(LabWork o) {
        if (this.id > o.id) return 1;
        if (this.id < o.id) return -1;
        return 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setMinimalPoint(Long minimalPoint) {
        this.minimalPoint = minimalPoint;
    }

    public void setPersonalQualitiesMinimum(Long personalQualitiesMinimum) {
        this.personalQualitiesMinimum = personalQualitiesMinimum;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Long getMinimalPoint() {
        return minimalPoint;
    }

    public Long getPersonalQualitiesMinimum() {
        return personalQualitiesMinimum;
    }

    public String getDescription() {
        return description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public String toString(){
        return "LabWork с id: " + id +'\n'
                + "Название Работы: " + name +'\n'
                + "Сложность: " + difficulty +'\n'
                + "Минимальный Балл(Выполнение/ЛичностныеКачества): " + minimalPoint + "/" + personalQualitiesMinimum +'\n'
                + "Описание работы: " + description;
    }

    public void replace(String name, Coordinates coordinates, Long minimalPoint, Long personalQualitiesMinimum, String description,
                        Difficulty difficulty){
        this.name = name;
        this.coordinates= coordinates;
        this.minimalPoint = minimalPoint;
        this.personalQualitiesMinimum = personalQualitiesMinimum;
        this.description = description;
        this.difficulty = difficulty;
    }
}

