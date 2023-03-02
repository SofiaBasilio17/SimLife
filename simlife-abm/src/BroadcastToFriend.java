import java.util.ArrayList;
import java.util.Map;

public class BroadcastToFriend implements BroadcastMediator {

    public void sendMessage(AgentMessages am, Agent a ) {
        // check for this agent's friends and deliver the message
        System.out.println("I am mediating messages");
        for (Agent friend : a.getFgroup().getFriends(a)){
            friend.perceiveMessage(am);
        }
    }
    public void sendPerception(Perception p, Agent a){
        for (Agent friend : a.getFgroup().getFriends(a)){
            friend.perceive(p);
        }
    }

}
