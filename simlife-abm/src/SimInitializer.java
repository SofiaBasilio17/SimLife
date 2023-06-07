import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class SimInitializer {
    // here will be the variables to initialize the simulation
    // e.g. number of agents, distributions, and so on
    private int agentNr;

    private Agent[] agents;

//    private Routine[] routineTypeA;
//    private Routine[] routineTypeB;
//    private Routine[] routineTypeC;


    public SimInitializer(int agentNr, List<Parameter> parameters, List<PerceptionRelationship> perceptionRelationships, List<MoveTo> movementActions, List<Acquire> acquireActions, List<Consume> consumeActions){
        this.agentNr = agentNr;
        this.createAgents(parameters, perceptionRelationships, movementActions, acquireActions, consumeActions);
    }

//    private void setRoutines(){
//        Routine routineSchool = new Routine("SCHOOL", TimeOfDay.EARLY_MORNING, Constraints.HARD);
//        Routine routineClass = new Routine("CLASS", TimeOfDay.MORNING, Constraints.HARD);
//        Routine routineCafeteria = new Routine("CAFETERIA", TimeOfDay.NOON, Constraints.SOFT);
//        Routine routineClassSecond = new Routine("CLASS", TimeOfDay.EARLY_NOON, Constraints.HARD);
//        Routine routineSports = new Routine("SPORTS", TimeOfDay.LATE_NOON, Constraints.HARD);
//        Routine routineCafe = new Routine("CAFE", Constraints.SOFT);
//        Routine routineHomeCurfew = new Routine("HOME", TimeOfDay.NIGHT, Constraints.SOFT);
//
//        this.routineTypeA = new Routine[]{ routineSchool, routineClass, routineCafeteria, routineClassSecond, routineSports, routineHomeCurfew };
//        this.routineTypeB = new Routine[]{ routineSchool, routineClass, routineCafeteria, routineClassSecond, routineHomeCurfew};
//        this.routineTypeC = new Routine[]{ routineSchool, routineClass, routineCafeteria, routineClassSecond, routineCafe };
//    }

    private ParameterState[] createParameterStates(List<Parameter> parameters){
        InteractionsMediator ii = new Interactions();
        Random rand = new Random();
        Double random_val;
        int parameterIdx = 0;
        // here we are also keeping track of the parameter names for easy access
        ParameterState[] states = new ParameterState[parameters.size()];
        String[] parameterNames = new String[parameters.size()];
        parameterIdx = 0;
        // TODO: Need to fix the initialization of parameters and perhaps with a settings file add the std, mean,
        //  and other important metrics to generate meaningful values
        for (Parameter p : parameters){

            if (p.getMin() == 0.0 && p.getMax() == 1.0 ){
                // with gaussian * standard deviation + mean
                // random_val = rand.nextGaussian() * 0.2 + 0.5;
                // double val = rand.nextGaussian() * 0.5 + 0.3;
                // TODO: Generate values using nextGaussian and then normalize them all

                double val = Double.valueOf(Math.random());
                BigDecimal bdUp=new BigDecimal(val).setScale(2, RoundingMode.UP);
                random_val = bdUp.doubleValue();
                // old random : random_val = Double.valueOf(Math.random());
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
    private ParameterState getParameterState(ParameterState[] parameterStates, Parameter p){
        // retrieve the parameter state that belongs to parameter p from an array of ParameterStates
        for (int i = 0; i < parameterStates.length; i++){
            if (parameterStates[i].getParam().equals(p)){
                return parameterStates[i];
            }
        }
        return null;

    }
    private Map<Perception, PerceptionStateRelationship> createPerceptionStates(ParameterState[] parameterStates, List<PerceptionRelationship> perceptionRelationships){
        Map <Perception, PerceptionStateRelationship> perceptionStateRelationships = new HashMap<Perception, PerceptionStateRelationship>();
        // for each perception
        int objectCount;
        for (PerceptionRelationship pr : perceptionRelationships){
            objectCount = 0;
            ParameterState[] parameterStateObjects = new ParameterState[pr.getObjectNr()];
            Parameter[] parameterObjects = pr.getObjects();
            // we need to retrieve the list of objects
            for ( Parameter p : parameterObjects){
                // for each parameter in the list of objects (in PerceptionRelationship)
                // we are interested in getting the parameterState that matches that Parameter
                ParameterState ps = getParameterState(parameterStates, p);
                if ( ps != null){
                    // if its not null then we add it to the parameterStateObjects array and increase the index counter
                    parameterStateObjects[objectCount] = ps;
                    objectCount += 1;
                }
            }
            PerceptionStateRelationship psr = new PerceptionStateRelationship(parameterStateObjects, pr);
            perceptionStateRelationships.put(pr.getSubject(), psr);
        }
        return perceptionStateRelationships;
    }
    private void createAgents(List<Parameter> parameters, List<PerceptionRelationship> perceptionRelationships, List<MoveTo> movementActions, List<Acquire> acquireActions, List<Consume> consumeActions){
        // for i=nr_agents,
        //      for p in parameters
        //          create the parameterState with a random initial value based on min and max
        //          add it to List<String, ParameterState> where String is the name of the parameter
        //      now we need to go through the parameters again and if they have a relationship we will create the ParameterStateRelationship by getting the objects in the List<String, ParameterState>
        //      create the agent
        // Mediators
        // InteractionsMediator takes care of dealing with interactions such as Parameter-Parameter and Perception-Parameter
        Agent[] agents = new Agent[this.agentNr];
        BroadcastMediator bc = new BroadcastToFriend();
        InteractionsMediator ii = new Interactions();

        for (int i = 0; i < this.agentNr; i++){
            // create the parameter states and the relationships for the agent
            ParameterState[] parameterStates = this.createParameterStates(parameters);
//            System.out.println("Agent " + i + " has " );
//            for (int j = 0 ; j < parameterStates.length ; j ++){
//                System.out.println(parameterStates[j].getParam().toString() + " = " + parameterStates[j].getCurrentValue());
//            }
            // create the perception state relationships (instead of linking to parameters,
            // the perceptionStateRelationships link the parameter states of the agent)
            Map<Perception, PerceptionStateRelationship> perceptionStateRelationships = createPerceptionStates(parameterStates, perceptionRelationships);
            // perceptionStateRelationships.forEach((k,v) -> System.out.println("Key = "
                    // + k + ", Nr of objects = " + v.getObjects().length));

            // TODO: Currently every agent has the same actions available (known), we may want to add knowledge of some locations
            Agent a = new Agent(i, true, bc, parameterStates, ii, perceptionStateRelationships, movementActions, acquireActions, consumeActions, null);
            agents[i] = a;
        }
        // TODO: turn this into proper friend group assignment
        FriendGroup fg = new FriendGroup(Arrays.asList(agents));
        for (int i = 0; i < this.agentNr; i++ ){
            agents[i].setFriendGroup(fg);
        }
        this.agents = agents;
    }
    public Agent[] getAgents(){
        return this.agents;
    }

}
