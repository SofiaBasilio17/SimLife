import java.util.ArrayList;
import java.util.Map;

public class BroadcastToFriend implements BroadcastMediator {

    public void sendPerception(Perception p, Agent a){
        for (Agent friend : a.getFgroup().getFriends(a)){
            friend.perceive(p);
        }
    }

}
