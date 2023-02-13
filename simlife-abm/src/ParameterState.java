import java.util.ArrayList;

public class ParameterState {


    private Parameter param;
    private Double currentValue;
    private Double initialValue;
    private ArrayList<Double> history;

    private InteractionsMediator ii;
    private ParameterStateRelationship psr;

//    public ParameterState(Parameter param, Integer currentValue){
//        this.param = param;
//        this.currentValue = Integer.valueOf(currentValue);
//        this.initialValue = Integer.valueOf(currentValue);
//        this.createHistory();
//    }

    public ParameterState(Parameter param, Double currentValue, InteractionsMediator ii){
        this.param = param;
        // this.currentValue = Double.valueOf(currentValue);
        // this.initialValue = Double.valueOf(currentValue);
        this.currentValue = currentValue;
        this.initialValue = currentValue;
        this.ii = ii;
        this.createHistory();
    }

    public void setParameterStateRelationship(ParameterStateRelationship psr){
        this.psr = psr;
    }

    private void createHistory(){
        this.history = new ArrayList<>();
    }

    public void updateValue(Double val){
        this.history.add(this.currentValue);
        this.currentValue = val;
        if ( this.psr != null){
            // call the mediator to take care of updates
            ii.internalInteraction(this);
        }


    }
    public Double getCurrentValue(){
        return this.currentValue;
    }
    public Double getPreviousValue(){
        if (this.history.isEmpty())
            return this.initialValue;
        return this.history.get(this.history.size() - 1);
    }
    public ArrayList<Double> getHistory() {
        return this.history;
    }
    public Parameter getParam(){
        return this.param;
    }
    public ParameterStateRelationship getParameterStateRelationship(){
        return this.psr;
    }
}
