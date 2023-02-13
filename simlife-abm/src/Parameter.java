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
    private ParamTypes type;

    private Double minimum;
    private Double maximum;

    private ParameterRelationship pr;

    public Parameter(String name_, Double minimum, Double maximum){
        this.parameterName = name_;
        this.type = ParamTypes.DOUBLE;
        this.minimum = Double.valueOf(minimum);
        this.maximum = Double.valueOf(maximum);
    }

    public void setParameterRelationship(ParameterRelationship pr){
        this.pr = pr;
    }

    @Override
    public String toString() {
        return "Parameter " + this.parameterName;
    }
    private Double getMin(){
        return this.minimum;
    }
    private Double getMax(){
        return this.maximum;
    }

}
