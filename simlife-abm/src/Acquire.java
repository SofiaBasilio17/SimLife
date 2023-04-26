public class Acquire extends Action{
    private String resource;
    private Double quantity;

    public Acquire(String actionName, String resource, Double quantity){
        this.setName(actionName);
        this.resource = resource;
        this.quantity = quantity;
    }

    public boolean isResource(String r){
        return this.resource.equalsIgnoreCase(r);
    }
}
