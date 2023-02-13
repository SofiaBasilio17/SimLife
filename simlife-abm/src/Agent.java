import java.util.*;
import java.util.Arrays;

// behavior class
// one method with behave
// routine, smoking behavior, it basically says behave
// array that contains behaviors, every time you call agent.act() you will go over the behavior array and call the behave()
// behaviors can be ordered by order of most urgent/important in terms of need
// behavior accesses the time of day

enum Actions {
    MOVETO,
}

enum AgentMessages{
    GOINGTOSMOKE,
    GREET,
    INVITE,
}


public class Agent {
    private String currPlace;
    private int id;
    // routine object
    private Routine[] routines;
    private Queue<Routine> routineQueue;
    private Boolean smokes;

    private BroadcastMediator bc;
    private InteractionsMediator ii;

    private ParameterState[] parameters;

    private FriendGroup fgroup;
    // have a list of friends, not every agent knows others
    // how do they become friends, call a function that becomeFriends? updateFriends? Friendship interface

    private Map<Perception,PerceptionStateRelationship> perceptionRelationships;
    // behavior class
    // when i create the agents i am sending in the behavior instances
    private String[] smokeWhere = new String[]{ "street", "school" };

    public Agent(int id, Routine[] routines, Boolean smokes, BroadcastMediator bc, ParameterState[] parameters, InteractionsMediator ii,  Map<Perception,PerceptionStateRelationship> perceptionRelationships) {
        this.id = id;
        this.routines = routines;
        this.currPlace = "HOME";
        this.routineQueue = new LinkedList<>();
        this.smokes = smokes;
        this.bc = bc;
        this.prepare_new_day();
        this.parameters = parameters;
        this.ii = ii;
        this.perceptionRelationships = perceptionRelationships;
    }
    @Override
    public String toString() {
        return "Agent " + this.id + " ";
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
        // creating a queue for the routines
        for(int i = 0 ; i < this.routines.length; i++){
            this.routineQueue.add(this.routines[i]);
        }
    }


    public void perceive(Perception percept){
        if (this.perceptionRelationships.containsKey(percept)){
            // send it to the mediator to do something about it
            System.out.println("I am perceiving " + percept.toString());
            ii.perceptionInteraction(perceptionRelationships.get(percept));
        }
    }

    public void perceiveMessage(AgentMessages am) {
        System.out.println(this + " ,am perceiving the message " + am + " from my friend");
    }

    public void perceive_time(String type, TimeOfDay message) {
        if (type == "time") {
            if ( message == TimeOfDay.EARLY_MORNING){
                this.prepare_new_day();
            }
            System.out.println("I am perceiving the time of day " + message);
            this.decide(message);

        }else {
            System.out.println("I ," + this.id + ", am perceiving");
        }
    }
    private void decide(TimeOfDay message) {
        System.out.println("I am deciding");
        // do check here nextPlace (routine)

        if (routineQueue.peek().getWhen() == message) {
            System.out.println("I need to move to " + this.routineQueue.peek().getWhere());
            this.act(Actions.MOVETO, "routine");
        } else {
            this.act(Actions.MOVETO, "outside");
        }
    }
    private void act(Actions action, String content) {
        System.out.println("I am acting");
        if (Actions.MOVETO == action) {
            if (content == "routine") {
                this.currPlace = this.routineQueue.remove().getWhere();
                System.out.println("I have moved to " + content);
            }else {
                this.currPlace = "street";
                System.out.println("I have moved to the street");
            }
        }
        // call the smoke before the moveTo
        if (Arrays.stream(smokeWhere).anyMatch(this.currPlace::equals)){
            System.out.println("====================");
            System.out.println(this.currPlace + " is a place I can smoke in");
            // goes smoking in his spot
            bc.sendMessage(AgentMessages.GOINGTOSMOKE, this);
            System.out.println("====================");
        }

    }
}
