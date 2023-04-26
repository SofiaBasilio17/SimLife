public class MoveTo extends Action{
    private String location;

    public MoveTo(String actionName, String location){
        this.setName(actionName);
        this.location = location ;
    }
    public void move(){
        System.out.println("i am " + this.name);
    }


    @Override
    public boolean equals(Object o) {
        if( o instanceof MoveTo){
            MoveTo a = (MoveTo) o;
            return this.name.equalsIgnoreCase(a.getName());
        }else if( o instanceof String){
            String a = (String) o;
            // System.out.println("Comparing "+ this.location.toLowerCase()+" and "+ a.toLowerCase());
            return this.location.equalsIgnoreCase(a);
        }
        return false;
    }


}
