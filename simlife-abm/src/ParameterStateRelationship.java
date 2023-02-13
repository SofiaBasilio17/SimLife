public class ParameterStateRelationship {
    private ParameterState subject;
    private ParameterState[] objects;
    private ParameterRelationship pr;

    public ParameterStateRelationship(ParameterState subject, ParameterState[] objects, ParameterRelationship pr) {
        this.subject = subject;
        this.objects = objects;
        this.pr = pr;
    }

    public ParameterState[] getObjects(){
        return this.objects;
    }

    public ParameterRelationship getParameterRelationship(){
        return this.pr;
    }

}
