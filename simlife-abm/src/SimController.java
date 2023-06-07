import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;



public class SimController {
    private Agent[] agentArray;
    private int iterations;

    public SimController(Agent[] agents,  int iterations ) {
        this.agentArray = agents;
        // iterations are days
        this.iterations = iterations;

    }
    public void loop(){
        // broadcast messages to neighbors
        // call action loops several times a day
        System.out.println("--------------------");
        System.out.println("SIM START");
        // loop through the nr of iterations
        for (int i = 0; i < this.iterations; i++) {
            System.out.println("######################");
            System.out.println("ITERATION "+i);
            for (int hoursOfDay = 7; hoursOfDay < 24 ; hoursOfDay++){
                System.out.println("---- Hour of day " + hoursOfDay + " ----");
                for (int agentIdx = 0; agentIdx < this.agentArray.length; agentIdx++) {
                    // at each timestep, we must call a function to change any parameters that are
                    // affected by time
                    agentArray[agentIdx].perceive_time(hoursOfDay);
                }
            }
            System.out.println("######################");
        }


    }
}
