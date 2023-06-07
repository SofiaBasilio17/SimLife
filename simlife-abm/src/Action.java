import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.jena.sparql.exec.http.Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Action {
    protected String name;
    // here we denote all the variables that action has
    // preconditions
    protected Preconditions preconditions;
    // parameter factors
    protected Map<Parameter, Double> parameterFactors;
    // state factors
    protected Map<State, Double> stateFactors;
    // commitment factors
    protected Commitment commitmentFactor;
    // commitment produced
    protected Commitment commitmentProduced;
    // state produced
    protected State stateProduced;
    // perception produced
    protected Perception perceptionProduced;
    // action relationship

    protected ActionRelationship actionRelationship;

    private BroadcastMediator bc;

    protected void setBroadcastMediator(BroadcastMediator bc){
        this.bc = bc;
    }

    protected void setName(String name){
        this.name = name;
    }

    protected void setParameterFactors(Map<Parameter, Double> paramfactors){
        this.parameterFactors = paramfactors;
    }
    protected void setPreconditions(Preconditions precon){
        this.preconditions = precon;
    }
    protected void setStateFactors(List<State> states, List<Double> coeff){
        for (int i = 0; i < states.size(); i++){
            this.stateFactors.put(states.get(i), coeff.get(i));
        }
    }
    protected void setCommitmentFactor(Commitment commFactor){
        this.commitmentFactor = commFactor;
    }

    protected void setCommitmentProduced(Commitment commProduced){
        this.commitmentProduced = commProduced;
    }

    protected void setStateProduced(State stateProduced){
        this.stateProduced = stateProduced;
    }

    protected void setPerceptionProduced(Perception percept){
        this.perceptionProduced = percept;
    }

    protected void setActionRelationship(ActionRelationship ar ){
        this.actionRelationship = ar;
    }

    public String getName(){ return this.name; }
    protected boolean isNameNull(){
        if (this.name == null){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String str = "--------------------\n" +"Action " + this.name + " \n";
        if(this.preconditions != null){
            str += this.preconditions.toString();
        }
        if(this.parameterFactors != null && !this.parameterFactors.isEmpty()){
            str += "Parameter Factors:\n";
            for (Map.Entry<Parameter, Double> entry : this.parameterFactors.entrySet()) {
                str += " * " + entry.getKey() + " Coefficient " + entry.getValue() + "\n";
            }
        }
        if(this.commitmentFactor != null){
            str += this.commitmentFactor.toString() + "\n";
        }
        if(this.perceptionProduced != null){
            str += "Perception Produced: "+ this.perceptionProduced.toString() + "\n";
        }
        if(this.actionRelationship != null){
            str += "Action Relationship: \n " + this.actionRelationship.toString()+ "\n";
        }
        str+= "--------------------\n";
        return str;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Action){
            Action a = (Action) o;
            System.out.println("Comparing "+ a.getName() + " and " + this.name);
            return this.name.equalsIgnoreCase(a.getName());
        }else if( o instanceof String){
            String a = (String) o;
            return this.name.equalsIgnoreCase(a);
        }
        return false;
    }

    public boolean isPossible(int currentTime){
        if (this.preconditions != null){
            // if it does have a time precondition, we check if the current time is within the time constraints
            return this.preconditions.isPossible(currentTime);
        }
        // then it has no time pre-condition and we can say that given a time possibility check, it is true
        return true;
    }

    public boolean isPossibleToAcquire(int currentTime){
        return this.preconditions.isPossibleToAcquire(currentTime);
    }
    protected Double calculatePreconditionsModifier(String location, int time){
        if (this.preconditions != null){
            return this.preconditions.preconditionsValue(location, time);
        }
        // if there are no pre-conditions, we still want to return the maximum value (all preconditions are fulfilled) otherwise there will be a skewed value towards those with preconditions
        return 1.0;
    }


    protected Double calculateParameterFactorsModifier(ParameterState[] parameterStates){
        if (this.parameterFactors != null && !this.parameterFactors.isEmpty()){
            List<Double> currentValues = new ArrayList<>();
            List<Double> coefficients = new ArrayList<>();
            for (Map.Entry<Parameter, Double> entry : this.parameterFactors.entrySet()) {
                for (ParameterState p : parameterStates){
                    if (p.getParam().equals(entry.getKey())){
                        // get that value for the parameterState
                        // add the normalized value
                        Double normalizedValue = (p.getCurrentValue() - p.getParam().getMin()) / (p.getParam().getMax() - p.getParam().getMin());
                        // System.out.println("VALUE OF " + p.toString() + " = " + p.getCurrentValue() + "  NORMALIZED = " + normalizedValue);
                        currentValues.add(normalizedValue);
                        coefficients.add(entry.getValue());
                    }
                }
            }
            Double finalValue = 0.0;
            for (int i = 0; i < currentValues.size(); i ++){
                finalValue += currentValues.get(i) * coefficients.get(i);
            }
            // need to normalize all te values in currentValues
            // multiply each by their coefficients and add them to the final value
            // return the final value
            System.out.println(" I have parameter factors with final value : " + finalValue);
            return finalValue + 0.5;
        }
        return 0.0;
    }

    protected Double calculateCommitmentFactorValue(int lastPerceivedTime){
        if (this.commitmentFactor != null){
            if(this.commitmentFactor.getTimeStart() == lastPerceivedTime){
                return 1.0;
            }
        }
        return 0.0;

    }

    public Double calculateActionValueGeneral(Agent agent) {
        // right now we are taking the agent because we are only keeping track of Parameters and not ParameterStates and we also need their location and list of resources for pre-conditions
        // possibility check, is action possible within the timeperiod

        // if it is possible, we can move on to checking the value
        // calculate preconditions modifier

        // calculate commitment modifier

        // calculate parameter factors modifier


        return 1.0;
    }

    public void executeGeneralAction(Agent agent){
        if ( this.perceptionProduced != null){
            // send message here
            bc.sendPerception(this.perceptionProduced, agent);
        }
    }


}
