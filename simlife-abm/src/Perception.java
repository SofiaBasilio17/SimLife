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

    @Override
    public boolean equals(Object o){
        if(o instanceof Perception){
            Perception a = (Perception) o;
            System.out.println("Comparing "+ a.getName() + " and " + this.name);
            return this.name.equalsIgnoreCase(a.getName());
        }else if( o instanceof String){
            String a = (String) o;
            return this.name.equalsIgnoreCase(a);
        }
        return false;
    }
}
