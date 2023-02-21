import javax.management.relation.Relation;
import java.util.ArrayList;

enum ParamTypes {
    DOUBLE,
    INTEGER,
    STRING,
    BOOL
}



public class Parameter {
    private String parameterName;
    private Double minimum;
    private Double maximum;

    private ParameterRelationship pr;

    public Parameter(String name_, Double minimum, Double maximum){
        this.parameterName = name_;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public void setParameterRelationship(ParameterRelationship pr){
        this.pr = pr;
    }

    @Override
    public String toString() {
        return "Parameter " + this.parameterName;
    }
    public Double getMin(){
        return this.minimum;
    }
    public Double getMax(){
        return this.maximum;
    }

    public String getParameterName() { return this.parameterName; }

    public String[] getFunctions(){
        return this.pr.getFunctions();
    }
    public String relationshipsToString(){
        if ( this.pr != null){
            return this.parameterName + pr.toString();
        }else {
            return this.parameterName + " effects no other parameters";
        }


    }

}
