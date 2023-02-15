public class ParameterRelationship {
    private Parameter[] objects;
    private String[] functions;
    public ParameterRelationship(Parameter[] objects, String[] functions){
        this.objects = objects;
        this.functions = functions;
    }
    public String[] getFunctions() {
        return this.functions;
    }

    public String getFunction(int i) {
        return this.functions[i];
    }
    @Override
    public String toString() {
        String printStatement = "";
        for (int i = 0; i < this.objects.length; i++){
            printStatement += "\n" + " ----> " + this.functions[i] + " ----> " + this.objects[i];
        }
        return printStatement;
    }

}


