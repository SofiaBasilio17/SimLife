import org.apache.jena.sparql.exec.http.Params;

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

    public Double calculateActionValue(Agent agent) {
        // right now we are taking the agent because we are only keeping track of Parameters and not ParameterStates



        return 1.0;
    }

}
