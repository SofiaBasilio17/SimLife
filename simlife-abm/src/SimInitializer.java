import java.util.*;

public class SimInitializer {
    // here will be the variables to initialize the simulation
    // e.g. number of agents, distributions, and so on
    private int agentNr;
    private double smokers;

    private Routine[] routineTypeA;
    private Routine[] routineTypeB;
    private Routine[] routineTypeC;

    public SimInitializer(int agentNr, double smokers){
        this.agentNr = agentNr;
        this.smokers = smokers;
        this.setRoutines();

    }

    private void setRoutines(){
        Routine routineSchool = new Routine("SCHOOL", TimeOfDay.EARLY_MORNING, Constraints.HARD);
        Routine routineClass = new Routine("CLASS", TimeOfDay.MORNING, Constraints.HARD);
        Routine routineCafeteria = new Routine("CAFETERIA", TimeOfDay.NOON, Constraints.SOFT);
        Routine routineClassSecond = new Routine("CLASS", TimeOfDay.EARLY_NOON, Constraints.HARD);
        Routine routineSports = new Routine("SPORTS", TimeOfDay.LATE_NOON, Constraints.HARD);
        Routine routineCafe = new Routine("CAFE", Constraints.SOFT);
        Routine routineHomeCurfew = new Routine("HOME", TimeOfDay.NIGHT, Constraints.SOFT);

        this.routineTypeA = new Routine[]{ routineSchool, routineClass, routineCafeteria, routineClassSecond, routineSports, routineHomeCurfew };
        this.routineTypeB = new Routine[]{ routineSchool, routineClass, routineCafeteria, routineClassSecond, routineHomeCurfew};
        this.routineTypeC = new Routine[]{ routineSchool, routineClass, routineCafeteria, routineClassSecond, routineCafe };
    }
    private ParameterState[] createParameterStates(List<Parameter> parameters){
        InteractionsMediator ii = new Interactions();
        Double random_val;
        int parameterIdx = 0;
        // here we are also keeping track of the parameter names for easy access
        ParameterState[] states = new ParameterState[parameters.size()];
        String[] parameterNames = new String[parameters.size()];
        parameterIdx = 0;
        for (Parameter p : parameters){
            if (p.getMin() == 0.0 && p.getMax() == 1.0 ){
                random_val = Double.valueOf(Math.random());
            }else{
                random_val = Double.valueOf((int)(Math.random()*(p.getMax().intValue()-p.getMin().intValue()+1)+p.getMin().intValue()));
            }

            ParameterState ps = new ParameterState(p,random_val,ii);
            states[parameterIdx] = ps;
            parameterNames[parameterIdx] = p.getParameterName();
            parameterIdx += 1;
        }
        for (int j = 0; j < parameters.size(); j++){
            // check if parameter has relationships
            if (parameters.get(j).containsRelationship()){
                // if it does then we retrieve each object's name and we create a list of ParameterStates
                // which will be the objects for that relationship
                String[] objNames = parameters.get(j).getObjectNames();
                // make a list of ParameterStates
                ParameterState[] objects = new ParameterState[objNames.length];
                for (int k = 0 ; k < objNames.length ; k++){
                    int psIndex = Arrays.asList(parameterNames).indexOf(objNames[k]);
                    objects[k] = states[psIndex];
                }
                // System.out.println("Parameter "+ parameters.get(j).getParameterName() + " has " + objects.length + " objects .");
                // create the ParameterStateRelationship for this ParameterState
                ParameterStateRelationship psr = new ParameterStateRelationship(objects);
                // set it for this ParameterState
                states[j].setParameterStateRelationship(psr);
            }
        }
        return states;
    }
    public void createAgents(List<Parameter> parameters, Map<Perception, PerceptionRelationship> perceptionRelationships){
        // for i=nr_agents,
        //      for p in parameters
        //          create the parameterState with a random initial value based on min and max
        //          add it to List<String, ParameterState> where String is the name of the parameter
        //      now we need to go through the parameters again and if they have a relationship we will create the ParameterStateRelationship by getting the objects in the List<String, ParameterState>
        //      create the agent
        // Mediators
        // InteractionsMediator takes care of dealing with interactions such as Parameter-Parameter and Perception-Parameter

        BroadcastMediator bc = new BroadcastToFriend();

        for (int i = 0; i < this.agentNr; i++){
            ParameterState[] parameterStates = this.createParameterStates(parameters);
            for (int j = 0 ; j < parameterStates.length ; j ++){
                System.out.println("Agent " + i + " has " + parameterStates[j].getParam().toString() + " = " + parameterStates[j].getCurrentValue());
            }
            // now we have the ParameterStates for this agent we can make the Map<Perception, PerceptionStateRelationships>
            // we need to make the agents

        }

    }

}
