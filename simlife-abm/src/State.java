public class State {
    private String stateName;

    public State(String stateName){
        this.stateName = stateName;
    }
    @Override
    public boolean equals(Object o){
        if( o instanceof State){
            State a = (State) o;
            return this.stateName == a.getStateName();
        }
        return false;

    }
    public String getStateName(){
        return this.stateName;
    }
}
