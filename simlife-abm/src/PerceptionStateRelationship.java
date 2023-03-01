public class PerceptionStateRelationship {
    private ParameterState[] objects;
    private PerceptionRelationship per;

    public PerceptionStateRelationship(ParameterState[] objects, PerceptionRelationship per){
        this.objects = objects;
        this.per = per;
    }
    public ParameterState[] getObjects() { return this.objects; }

    public PerceptionRelationship getPerceptionRelationship() { return this.per; }

}
