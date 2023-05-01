public class TimePeriod {
    private int periodStart ;
    private int periodEnd;

    public TimePeriod(int periodStart, int periodEnd){
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }
    public int getPeriodStart(){ return this.periodStart; }
    public int getPeriodEnd(){ return this.periodEnd; }

    public boolean isWithinTimePeriod(int time){
        if(this.periodStart <= time && this.periodEnd > time){
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return "Time period - Start: " + this.periodStart + ", End: " + this.periodEnd;
    }
}
