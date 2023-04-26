public class Commitment {
    private TimePeriod timePeriod;
    private Action action;
    public Commitment(Action a, TimePeriod t){
        this.action = a;
        this.timePeriod = t;
    }
    @Override
    public String toString() {
        return "Commitment Action: " + this.action.getName() + " " +  this.timePeriod.toString() + "\n";
    }

}
