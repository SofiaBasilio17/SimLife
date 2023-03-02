import java.util.ArrayList;
import java.util.List;

public class FriendGroup {
    private List<Agent> group;

    public FriendGroup (List<Agent> g){
        this.group = g;
    }

    @Override
    public String toString(){
        String output = "[ ";
        for ( Agent a : this.group){
            output += a.toString();
        }
        output += " ]";
        return output;
    }

    public ArrayList<Agent> getFriends(Agent a) {
        ArrayList<Agent> agroup = new ArrayList<Agent>(group);
        agroup.remove(a);
        System.out.println("The group " + this.group);
        // System.out.println("The group without the agent " + agroup);
        return agroup;
    }

    public List<Agent> getAgents(){
        return this.group;
    }


}
