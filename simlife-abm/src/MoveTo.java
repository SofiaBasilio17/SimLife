import java.util.Map;

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
    public Double calculateActionValue(String location, Map<String, Double> resources, ParameterState[] parameterStates, int lastPerceivedTime){
        // get the preconditions modifier from the class Action and add it the posession (or non-posession of the resource and quantity)
        Double preconditionsModifier = this.calculatePreconditionsModifier(location, lastPerceivedTime);
        // get the parameterFactors modifier
        Double parameterFactorsModifier = this.calculateParameterFactorsModifier(parameterStates);
        // get the commitment factor modifier
        Double commitmentFactorModifier = this.calculateCommitmentFactorValue(lastPerceivedTime);
        // return action value
        Double finalActionValue = parameterFactorsModifier + commitmentFactorModifier + preconditionsModifier;

        // System.out.println("Final value calculation = " + parameterFactorsModifier + " + " + commitmentFactorModifier + " + " + preconditionsModifier);
        return finalActionValue;
    }
    public void executeAction(Agent a){
        System.out.println("I am executing action " + this.name);
        a.move(this.location, this.commitmentFactor);
    }

}
