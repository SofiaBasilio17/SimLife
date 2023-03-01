import org.apache.jena.sparql.exec.http.Params;

public class PerceptionRelationship {

    private Perception subject;
    private Parameter[] objects;
    private String[] functions;

    public PerceptionRelationship(Perception subject, Parameter[] objects, String[] functions){
        this.subject = subject;
        this.objects = objects;
        this.functions = functions;
    }

    @Override
    public String toString() {
        String printStatement = "";
        printStatement += subject.toString();
        for (int i = 0; i < this.objects.length; i++){
            printStatement += "\n" + " ----> " + this.functions[i] + " ----> " + this.objects[i];
        }
        return printStatement;
    }

    public Perception getSubject(){ return this.subject; }
    public int getObjectNr(){
        return this.objects.length;
    }
    public String getFunction(int i) {
        return this.functions[i];
    }
    public Parameter[] getObjects(){ return this.objects; }
}
