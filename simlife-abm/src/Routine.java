// method nextPlace
// list of places hidden in routine class

public class Routine {
    // put the array here of the routine places
    // it keeps track of routines
    private String where;
    private TimeOfDay when;
    private Constraints constraint;
    private Boolean hasTimeConstrain;

    public Routine(String where, TimeOfDay when, Constraints constraint) {
        this.where = where;
        this.when = when;
        this.constraint = constraint;
        this.hasTimeConstrain = true;
    }
    public Routine(String where, Constraints constraint) {
        this.where = where;
        this.constraint = constraint;
        this.hasTimeConstrain = false;
    }

    public Boolean onTime() {
        // does this routine have a timing to it?
        return this.hasTimeConstrain;
    }
    public Constraints whichConstraint(){
        // Hard constraint is something like school
        // Soft constraint is something like going to a cafe or park after school
        return this.constraint;
    }
    public TimeOfDay getWhen(){
        // when does this need to be done (usually)
        return this.when;
    }
    public String getWhere() {
        return this.where;
    }
}
