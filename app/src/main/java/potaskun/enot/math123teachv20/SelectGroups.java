package potaskun.enot.math123teachv20;

public class SelectGroups {
    private String name;
    private int id;
    private int idLess;

    public SelectGroups(String name, String id, String idLess){
        this.name = name;
        this.id   = Integer.parseInt(id);
        this.idLess = Integer.parseInt(idLess);
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

    public  void setIdLess(int idLess){ this.idLess = idLess;};

    public int getIdLess() {
        return idLess;
    }
}
