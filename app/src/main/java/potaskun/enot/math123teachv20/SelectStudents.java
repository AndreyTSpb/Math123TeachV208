package potaskun.enot.math123teachv20;

public class SelectStudents {
    private String name; //имя студента
    private int id; // айди студента
    private int pas; //был иле небыл на занятии
    private int dog; //сдал договор или нет

    public SelectStudents(String name, int id, int pas, int dog){
        this.name = name;
        this.id   = id;
        this.pas  = pas;
        this.dog  = dog;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDog() {
        return dog;
    }

    public void setDog(int dog) {
        this.dog = dog;
    }

    public int getPas() {
        return pas;
    }

    public void setPas(int pass) {
        this.pas = pass;
    }
}
