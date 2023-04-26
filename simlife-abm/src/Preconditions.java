public class Preconditions {
    // TODO: Precondition location may be a list of locations
    // E.G. you can buy cigarettes at different stores
    // time periods and resources may also be a list,
    // e.g. if you are hungry you may want to consume a resource of type food
    // e.g. a store may be open in two different periods, lunch break being the one that breaks the period
    private String name;
    private String location;
    private String resource;

    private TimePeriod time;

    public Preconditions(String name){
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public void setResource(String resource){
        this.resource = resource;
    }
    public void setTime(TimePeriod time) {
        this.time = time;
    }
    @Override
    public String toString() {
        String str = "Preconditions:\n";
        if (time != null){
            str += "* time " + this.time.toString() + "\n";
        }
        if(location != null){
            str += "* location " + this.location + "\n";
        }
        if (resource != null){
            str += "* resource " + this.resource + "\n";
        }
        return str;
    }
}
