public interface InteractionsMediator {
    public Boolean isVariable(String str);
    public Boolean isOperator(String str);
    public Double calculateParameterEffect(ParameterState subject, ParameterState object, String function);
    public Double calculatePerceptionEffect(ParameterState object, String function);
    public void internalInteraction(ParameterState ps);
    public void perceptionInteraction(PerceptionStateRelationship relationship);
}
