import java.util.Map;

public class Consume extends Action{
    private String resource;
    private Double quantity;

    private Acquire acquireAction;
    public Consume(String actionName, String resource, Double quantity){
        this.setName(actionName);
        this.resource = resource;
        this.quantity = quantity;

    }

    public boolean isResource(String r){
        return this.resource.equalsIgnoreCase(r);
    }

    public String getResource(){ return this.resource; }
    public boolean isEnoughToConsume(Double quantity){
        return this.quantity <= quantity;
    }
    public Double calculateActionValue(String location, Map<String, Double> resources, ParameterState[] parameterStates, int lastPerceivedTime){
        // get the preconditions modifier from the class Action and add it the posession (or non-posession of the resource and quantity)
        Double preconditionsModifier = this.calculatePreconditionsModifier(location, lastPerceivedTime);
        // get the parameterFactors modifier
        Double parameterFactorsModifier = this.calculateParameterFactorsModifier(parameterStates);
        // get the commitment factor modifier
        Double commitmentFactorModifier = this.calculateCommitmentFactorValue(lastPerceivedTime);
        // return action value
        Double finalActionValue = parameterFactorsModifier +commitmentFactorModifier + preconditionsModifier;
        // System.out.println("Final value calculation = " + parameterFactorsModifier + " + " + commitmentFactorModifier + " - " + preconditionsModifier);
        return finalActionValue;
    }

    public void executeAction(Agent a, String location, boolean needToAcquire){
        System.out.println("I am executing action " + this.name);
        // if not all preconditions are fulfilled we need to first execute in this order : acquire -> moveTo
        if (needToAcquire){
            this.preconditions.getResourceAction().executeAction(a, location);
        }else{
            if(this.preconditions != null && this.preconditions.hasLocation() && !this.preconditions.matchesLocation(location)){
                // we need to execute the move to
                this.preconditions.getLocationAction().executeAction(a);
            }else {
                // if the action can be executed immediately
                a.consumeResource(this.resource, this.quantity);
            }
        }


    }

}
