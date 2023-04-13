import javax.management.relation.Relation;
import java.util.ArrayList;



public class Parameter {
    private String parameterName;
    private Double minimum;
    private Double maximum;

    private boolean hasRelationships;
    private ParameterRelationship pr;

    public Parameter(String name_, Double minimum, Double maximum){
        this.parameterName = name_;
        this.minimum = minimum;
        this.maximum = maximum;
        this.hasRelationships = false;
    }

    public void setParameterRelationship(ParameterRelationship pr){
        this.pr = pr;
        this.hasRelationships = true;
    }

    @Override
    public String toString() {
        return "Parameter " + this.parameterName;
    }

    @Override
    public boolean equals(Object o){
        if( o instanceof Parameter){
            Parameter a = (Parameter) o;
            return this.parameterName == a.getParameterName();
        }
        return false;

    }
    public Double getMin(){
        return this.minimum;
    }
    public Double getMax(){
        return this.maximum;
    }


    public String getParameterName() { return this.parameterName; }

    public String[] getObjectNames(){
        return this.pr.getObjectNames();
    }
    public String[] getFunctions(){
        return this.pr.getFunctions();
    }
    public String relationshipsToString(){
        if ( this.pr != null){
            return this.parameterName + pr.toString();
        }else {
            return this.parameterName + " affects no other parameters";
        }
    }
    public boolean containsRelationship(){ return this.hasRelationships; }

}
