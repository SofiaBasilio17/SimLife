import java.lang.reflect.Array;
import java.util.*;
import java.util.Arrays;

// behavior class
// one method with behave
// routine, smoking behavior, it basically says behave
// array that contains behaviors, every time you call agent.act() you will go over the behavior array and call the behave()
// behaviors can be ordered by order of most urgent/important in terms of need
// behavior accesses the time of day

// TODO: Actions that are tied to a commitment should not affect the value for that action when in a commitment for that location
// TODO: Initialize agents according to distributions
// TODO: Time needs to be adjusted (currently its an hour per tick but it should be smaller, perhaps 15 mins?)
// TODO: Add the perception effect when an action is implemented within the action class


public class Agent {
    private String location;
    private Map<String, Double> resources;
    private int id;
    private Boolean smokes;
    private BroadcastMediator bc;
    private InteractionsMediator ii;
    private ParameterState[] parameters;
    private FriendGroup fgroup;
    // have a list of friends, not every agent knows others
    // how do they become friends, call a function that becomeFriends? updateFriends? Friendship interface
    private List<MoveTo> movementActions ;
    private List<Acquire> acquireActions ;
    private List<Consume> consumeActions ;
    private List<Action> otherActions;
    private Map<Perception,PerceptionStateRelationship> perceptionRelationships;
    private boolean inCommitment;
    private Commitment currentCommitment;
    private int lastPerceivedTime;


    public Agent(int id,
                 Boolean smokes,
                 BroadcastMediator bc,
                 ParameterState[] parameters,
                 InteractionsMediator ii,
                 Map<Perception,PerceptionStateRelationship> perceptionRelationships,
                 List<MoveTo> movementActions, List<Acquire> acquireActions, List<Consume> consumeActions, List<Action> otherActions) {
        this.id = id;
        this.location = "Home";
        this.smokes = smokes;
        this.bc = bc;
        this.prepare_new_day();
        this.parameters = parameters;
        this.ii = ii;
        this.perceptionRelationships = perceptionRelationships;
        this.movementActions = movementActions;
        this.acquireActions = acquireActions;
        this.consumeActions = consumeActions;
        this.otherActions = otherActions;
        this.resources = new HashMap<>();
        this.inCommitment = false;
    }
    @Override
    public String toString() {
        String str = " I am agent " + this.id + "\n";
        if (this.parameters.length > 0){
            for(int i = 0; i < this.parameters.length; i++){
                str+= " * " + this.parameters[i].toString() + "\n";
            }
        }
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof Agent){
            Agent a = (Agent) o;
            return this.id == a.id;
        }
        return false;
    }

    public void setFriendGroup(FriendGroup fg ){
        this.fgroup = fg;
    }

    public FriendGroup getFgroup(){
        return this.fgroup;
    }

    private void prepare_new_day() {
        // commitments should be reloaded perhaps
    }
    public void perceive(Perception percept){
        if (this.perceptionRelationships.containsKey(percept)){
            // send it to the mediator to do something about it
            System.out.println("I am perceiving " + percept.toString());
            ii.perceptionInteraction(perceptionRelationships.get(percept));
        }
    }

    public void perceive_time(int time) {
        this.lastPerceivedTime = time;
        if (this.inCommitment){
            if (time == this.currentCommitment.getTimeEnd()){
                this.inCommitment = false;
                this.currentCommitment = null;
            }
        }
//        if (type == "time") {
//            if ( message == TimeOfDay.EARLY_MORNING){
//                this.prepare_new_day();
//            }
//            System.out.println("I am perceiving the time of day " + message);
//            this.decide(message);
//
//        }else {
//            System.out.println("I ," + this.id + ", am perceiving");
//        }
        this.decide();
    }
    private void decide() {
        // TODO: commitments which have been fulfilled should not be in the list anymore of commitments..
        System.out.println("I am deciding");
        List<Double> moveToActionValues = new ArrayList<>();
        List<Double> acquireActionValues = new ArrayList<>();
        List<Double> consumeActionValues = new ArrayList<>();

        Double moveToMaximum = 0.0;
        int moveToIndex = 0;
        Double acquireMaximum = 0.0;
        int acquireIndex = 0;
        Double consumeMaximum = 0.0;
        int consumeIndex = 0;
        for (int i = 0 ; i < this.movementActions.size(); i++){
            if (!this.movementActions.get(i).equals(this.location)){
                if (this.movementActions.get(i).isPossible(this.lastPerceivedTime)){
                    // System.out.println("Final value for " + this.movementActions.get(i).getName() + " = " + this.movementActions.get(i).calculateActionValue(this.location, this.resources, this.parameters, this.lastPerceivedTime));
                    Double value = this.movementActions.get(i).calculateActionValue(this.location, this.resources, this.parameters, this.lastPerceivedTime);
                    if (this.inCommitment){
                        value -= 1.5;
                    }
                    if (moveToMaximum < value){
                        moveToMaximum = value;
                        moveToIndex = i;
                    }
                }
            }
        }
        for (int i = 0 ; i < this.acquireActions.size(); i++){
            if( this.acquireActions.get(i).isPossible(this.lastPerceivedTime)){
                // System.out.println("Final value for " + this.acquireActions.get(i).getName() + " = " + this.acquireActions.get(i).calculateActionValue(this.location, this.resources, this.parameters, this.lastPerceivedTime));
                Double value = this.acquireActions.get(i).calculateActionValue(this.location, this.resources, this.parameters, this.lastPerceivedTime);
                if (this.inCommitment){
                    value -= 1.5;
                }
                if (acquireMaximum < value){
                    acquireMaximum = value;
                    acquireIndex = i;
                }
            }

        }
        for (int i = 0 ; i < this.consumeActions.size(); i++){
            // do i have the resource AND is it enough for the consume action OR am i able to acquire it at this time
            if (
                    (this.resources.containsKey(this.consumeActions.get(i).getResource())
                            &&
                            this.consumeActions.get(i).isEnoughToConsume(this.resources.get(this.consumeActions.get(i).getResource())))
                    ||
                    this.consumeActions.get(i).isPossibleToAcquire(this.lastPerceivedTime))
            {
                // then we check if the consumption is actually possible
                if (this.consumeActions.get(i).isPossible(this.lastPerceivedTime)){
                    // System.out.println("Final value for " + this.consumeActions.get(i).getName() + " = " + this.consumeActions.get(i).calculateActionValue(this.location, this.resources, this.parameters, this.lastPerceivedTime));
                    Double value = this.consumeActions.get(i).calculateActionValue(this.location, this.resources, this.parameters, this.lastPerceivedTime);
                    if (this.inCommitment){
                        value -= 1.5;
                    }
                    if (consumeMaximum < value){
                        consumeMaximum = value;
                        consumeIndex = i;
                    }
                }
            }
        }
//        System.out.println("Maximum value for move to actions = " + moveToMaximum);
//        System.out.println("Maximum value for acquire actions = " + acquireMaximum);
//        System.out.println("Maximum value for consume actions = " + consumeMaximum );
        if (moveToMaximum > acquireMaximum && moveToMaximum > consumeMaximum){
            System.out.println("I want to " + this.movementActions.get(moveToIndex).getName() + " with value " + moveToMaximum);
            this.movementActions.get(moveToIndex).executeAction(this);
            // this.act("move", this.movementActions.get(moveToIndex));
        }else if( acquireMaximum > moveToMaximum && acquireMaximum > consumeMaximum){
            System.out.println("I want to " + this.acquireActions.get(acquireIndex).getName() + " with value " + acquireMaximum);
            this.acquireActions.get(acquireIndex).executeAction(this, this.location);
            // this.act("acquire", this.acquireActions.get(acquireIndex));
        }else if ( consumeMaximum > moveToMaximum && consumeMaximum > acquireMaximum){
            System.out.println("I want to " + this.consumeActions.get(consumeIndex).getName() + " with value " + consumeMaximum);
            // if we have enough of the resource then we consume it
            if(this.resources.containsKey(this.consumeActions.get(consumeIndex).getResource()) && this.consumeActions.get(consumeIndex).isEnoughToConsume(this.resources.get(this.consumeActions.get(consumeIndex).getResource()))){
                this.consumeActions.get(consumeIndex).executeAction(this, this.location, false);
            }else{
                this.consumeActions.get(consumeIndex).executeAction(this, this.location, true);
            }


            // this.act("consume",this.consumeActions.get(consumeIndex));
        }else {
            System.out.println("I want to do nothing");
        }


        // if in a commitment, likelihood of selecting an moveto action is very low
        // perhaps they get a modifier that could make them negative and if there are any positive, you could move somewhere else

        // get all action values, pick highest

    }
    private void act(String type, Action action) {
        System.out.println("I am acting");


    }



    public void consumeResource(String resource, Double quantity){
        System.out.println("Consuming " + resource + "  current value " + this.resources.get(resource) + " consuming " + quantity.toString());
        this.resources.put(resource, this.resources.get(resource) - quantity);
    }
    public void acquireResource(String resource, Double quantity){
        // if the resource is already in the resources, we add the quantity to it
        if( this.resources.containsKey(resource)){
            System.out.println("Acquiring " + resource + "  current value " + this.resources.get(resource) + " acquiring " + quantity.toString());
            this.resources.put(resource, this.resources.get(resource) + quantity);
        // otherwise we have to put it
        }else{
            System.out.println("Acquiring " + resource + " acquiring " + quantity.toString());
            this.resources.put(resource, quantity);
        }
    }
    public void move(String location, Commitment commitment){
        this.location = location;
        if (commitment != null){
            this.inCommitment = true;
            this.currentCommitment = commitment;
        }
    }
}
