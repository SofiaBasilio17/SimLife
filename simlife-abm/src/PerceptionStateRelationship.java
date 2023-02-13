public class PerceptionStateRelationship {
    private Perception subject;
    private ParameterState[] objects;
    private PerceptionRelationship per;

    public PerceptionStateRelationship(Perception subject, ParameterState[] objects, PerceptionRelationship per){
        this.subject = subject;
        this.objects = objects;
        this.per = per;
    }
    public ParameterState[] getObjects() { return this.objects; }

    public PerceptionRelationship getPerceptionRelationship() { return this.per; }

}
