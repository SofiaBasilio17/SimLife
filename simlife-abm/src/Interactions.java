import java.util.Stack;

public class Interactions implements InteractionsMediator{
    public Boolean isVariable(String str){
        return str.equals("SCURRENT") || str.equals("SPREVIOUS") || str.equals("OCURRENT") ;
    }
    public Boolean isOperator(String str){
        return str.equals("+") || str.equals("-") || str.equals("*") || str.equals("/");
    }
    public Double calculateParameterEffect(ParameterState subject, ParameterState object, String function){
        // do prefix notation
        Stack<Double> operands = new Stack<Double>();
        for (String word : function.split(" ")){
            if (isVariable(word)){
                // check if it is variable
                // we need to add the actual value to the stack
                // POSSIBLE VALUES : SCURRENT (subject current value), SPREVIOUS (subject previous value), OCURRENT (object current value)
                // System.out.println(word + " is a variable");
                switch (word){
                    case "SCURRENT":
                        operands.push(subject.getCurrentValue().doubleValue());
                        break;
                    case "SPREVIOUS":
                        operands.push(subject.getPreviousValue().doubleValue());
                        break;
                    default:
                        operands.push(object.getCurrentValue().doubleValue());
                }

            }else if( isOperator(word)){
                // check if it is operator
//                System.out.println(word + " is an operator");
//                System.out.println("The stack " + operands);
                double o1 = operands.peek();
                operands.pop();
                double o2 = operands.peek();
                operands.pop();
                switch (word){
                    case "+":
                        operands.push(o2 + o1);
                        // System.out.print(o2 + " + " + o1 + " ");
                        break;
                    case "-":
                        operands.push(o2 - o1);
                        // System.out.print(o2 + " - " + o1 + " ");
                        break;
                    case "*":
                        operands.push(o2 * o1);
                        // System.out.print(o2 + " * " + o1 + " ");
                        break;
                    case "/":
                        operands.push(o2 / o1);
                        // System.out.print(o2 + " / " + o1 + " ");
                        break;
                }
                // System.out.println();
            }else {
                // else it is a Double (number)
                // System.out.println(word + " is an operand");
                operands.push(Double.parseDouble(word));
            }
        }
        return operands.pop();
    }

    public void internalInteraction(ParameterState ps){
        // what we need here is the objects from psr.getObjects()
        // and we need the functions which will be ps.getParam().getFunctions()
        String[] functions = ps.getParam().getFunctions();
        // ParameterRelationship pr = psr.getParameterRelationship();
        ParameterState[] objects = ps.getParameterStateRelationship().getObjects();
        for (int i = 0; i < objects.length; i++){
            // calculate this object's new value
            // Double updatedObjectValue = calculateParameterEffect(ps, objects[i], pr.getFunction(i));
            Double updatedObjectValue = calculateParameterEffect(ps, objects[i], functions[i]);
            System.out.println("The new value for " + objects[i].getParam().toString() + " is " + updatedObjectValue);
            // send it to the object to update
            objects[i].updateValue(updatedObjectValue);
        }
    }

    public Double calculatePerceptionEffect(ParameterState object, String function){
        // do prefix notation
        Stack<Double> operands = new Stack<Double>();
        for (String word : function.split(" ")){
            if (isVariable(word)){
                // check if it is variable
                // we need to add the actual value to the stack
                // POSSIBLE VALUES : OCURRENT (object current value)
                // TODO: could potentially add OPREVIOUS but not necessary right now
                // System.out.println(word + " is a variable");
                operands.push(object.getCurrentValue().doubleValue());
            }else if( isOperator(word)){
                // check if it is operator
                // System.out.println(word + " is an operator");
                // System.out.println("The stack " + operands);
                double o1 = operands.peek();
                operands.pop();
                double o2 = operands.peek();
                operands.pop();
                switch (word){
                    case "+":
                        operands.push(o2 + o1);
                        // System.out.print(o2 + " + " + o1 + " ");
                        break;
                    case "-":
                        operands.push(o2 - o1);
                        // System.out.print(o2 + " - " + o1 + " ");
                        break;
                    case "*":
                        operands.push(o2 * o1);
                        // System.out.print(o2 + " * " + o1 + " ");
                        break;
                    case "/":
                        operands.push(o2 / o1);
                        // System.out.print(o2 + " / " + o1 + " ");
                        break;
                }
                // System.out.println();
            }else {
                // else it is a Double (number)
                // System.out.println(word + " is an operand");
                operands.push(Double.parseDouble(word));
            }
        }
        return operands.pop();
    }

    public void perceptionInteraction(PerceptionStateRelationship relationship) {
        // take care of updating all the ParameterStates connected to the perception (objects in the PerceptionStateRelationship)
        ParameterState[] objects = relationship.getObjects();
        for (int i = 0; i < objects.length; i++){
            // calculate this object's new value
            // System.out.println(" ===== ");
            // System.out.println("Updating value for " + objects[i].getParam().toString());
            // System.out.println("From " + objects[i].getCurrentValue());
            // TODO: get comparison of the current value with the max here
            Double updatedObjectValue = calculatePerceptionEffect(objects[i], relationship.getPerceptionRelationship().getFunction(i));
            // System.out.println("To " + updatedObjectValue);
            // System.out.println(" ===== ");
            // send it to the object to update
            objects[i].updateValue(updatedObjectValue);
        }
    }
}
