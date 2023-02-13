public class ParameterRelationship {
    private Parameter subject;
    private Parameter[] objects;
    private String[] functions;
    public ParameterRelationship(Parameter subject, Parameter[] objects, String[] functions){
        this.subject = subject;
        this.objects = objects;
        this.functions = functions;
    }
    public String getFunction(int i) {
        return this.functions[i];
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

}


