public class Perception {
    // private int id;
    private String name;

    public Perception(String name){
        // this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Perception " + this.name;
    }

    public String getName(){
        return this.name;
    }
}
