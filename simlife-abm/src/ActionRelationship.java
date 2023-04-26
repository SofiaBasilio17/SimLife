public class ActionRelationship {
    private Action subject;
    private Parameter[] objects;
    private String[] functions;

    public ActionRelationship(Action subject, Parameter[] objects, String[] functions){
        this.subject = subject;
        this.objects = objects;
        this.functions = functions;
    }

    @Override
    public String toString() {
        String printStatement = "";
        printStatement += subject.getName();
        for (int i = 0; i < this.objects.length; i++){
            printStatement += "\n" + " ----> " + this.functions[i] + " ----> " + this.objects[i];
        }
        return printStatement;
    }

    public Action getSubject(){ return this.subject; }
    public int getObjectNr(){
        return this.objects.length;
    }
    public String getFunction(int i) {
        return this.functions[i];
    }
    public Parameter[] getObjects(){ return this.objects; }
}
