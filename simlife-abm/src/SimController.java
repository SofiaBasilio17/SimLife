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
    private Agent[] agentArray;
    private int iterations;
    private ArrayList<FriendGroup> friendGroups;

    public SimController(Agent[] agents,  int iterations ) {
        this.agentArray = agents;
        // iterations are days
        this.iterations = iterations;
        this.friendGroups = new ArrayList<>();
    }



    public void loop(){
        // broadcast messages to neighbors
        // call action loops several times a day

        // loop through the nr of iterations
        for (int i = 0; i < this.iterations; i++) {
            for( TimeOfDay t : TimeOfDay.values()) {
                // in every iteration, agents perceive "time"
                for (int j = 0; j < this.agentArray.length; j++) {
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
