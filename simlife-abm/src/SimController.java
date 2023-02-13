import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

enum TimeOfDay {
    EARLY_MORNING,
    MORNING,
    NOON,
    EARLY_NOON,
    MIDDLE_NOON,
    LATE_NOON,
    NIGHT
}

enum Constraints {
    SOFT,
    HARD
}

public class SimController {
    private int agentNr;
    private Agent[] agentArray;
    private int iterations;
    private Routine[] routineTypeA;
    private Routine[] routineTypeB;
    private Routine[] routineTypeC;
    private int smokers;
    private ArrayList<FriendGroup> friendGroups;

    public SimController(int agentNr, int iterations, int smokers) {
        this.agentNr = agentNr;
        this.agentArray = new Agent[this.agentNr];
        // iterations are days
        this.iterations = iterations;
        this.smokers = smokers;
        this.friendGroups = new ArrayList<>();
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

    public void initialize() {

        BroadcastMediator bc = new BroadcastToFriend();
        // create the agents
        int smoker_counter = this.smokers;
        // COMMENTED TO FIX ADDING PARAMETERS
//        for (int i = 0; i < this.agentNr; i++) {
//            if (smoker_counter > 0 ){
//                this.agentArray[i] = new Agent(i, this.routineTypeB, true, bc);
//            }
//            this.agentArray[i] = new Agent(i, this.routineTypeB, false, bc);
//        }

        int friendsPerGroup = 3;
        int nrGroups = this.agentNr / friendsPerGroup;
        System.out.println(nrGroups);
        ArrayList<Agent> group = new ArrayList<>();
        int assignmentCounter = 0;

        // we create a new friends group array
        while (assignmentCounter < this.agentNr) {
            group.add(this.agentArray[assignmentCounter]);
            // if length of arraylist == friendspergroup
            // call the constructor for FriendsGroup
            // send it to the agent
            // clear the arrayList
            // System.out.print("The size: " + group.size());
            if (group.size() == friendsPerGroup || assignmentCounter == this.agentNr-1){
                this.friendGroups.add(new FriendGroup(group));
                // System.out.println(this.friendGroups);
                group = new ArrayList<>();
            }
            assignmentCounter += 1;
        }

        System.out.println(" friend groups : " + this.friendGroups);

        for ( FriendGroup fg : this.friendGroups){
            for (Agent a : fg.getAgents() ){
                a.setFriendGroup(fg);
            }
        }
        System.out.println(this.agentArray[0].getFgroup());
    }

    public void loop(){
        // broadcast messages to neighbors
        // call action loops several times a day

        // loop through the nr of iterations
        for (int i = 0; i < this.iterations; i++) {
            for( TimeOfDay t : TimeOfDay.values()) {
                // in every iteration, agents perceive "time"
                for (int j = 0; j < this.agentNr; j++) {
                    agentArray[j].perceive_time("time", t);
                }
            }
            // agents need to prepare for a new day
//            for (int j = 0; j < this.agentNr; j++) {
//                agentArray[j].perceive("new_day");
//            }
        }


    }
}
