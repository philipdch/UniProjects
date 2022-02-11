import minipython.analysis.DepthFirstAdapter;
import minipython.node.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/* This visitor is used in the second passing.
Given the function and variable definitions found in the first passing,
we will check if a variable has been declared before its use, as well as
if the arguments passed in a function match its definition.
 */
public class SecondVisitor extends DepthFirstAdapter {
    //first two tables are partially filled from the first passing
    private Hashtable<String, List<FunctionArgs>> functions; //table used for function definitions
    private Hashtable<String, Type> vars;
    //table used when we encounter a function call
    private Hashtable<String, FunCall> fcalls;

    private FunCall currentFunc; //keep the most recently found function definition
    private int currentLine;
    private boolean returning;

    public SecondVisitor(Hashtable<String, List<FunctionArgs>> functions, Hashtable<String, Type> vars, Hashtable<String, FunCall> fcalls) {
        this.functions = functions;
        this.vars = vars;
        this.fcalls = fcalls;
    }

    //Assignment statements
    @Override
    public void inAAssignStatement(AAssignStatement node) {
        String varName = Utils.extractId(node.getIdentifier()).toString();
        Type type = Utils.extractType(node.getExpression(), functions, vars, currentFunc);
        vars.put(varName, type);
    }

    @Override
    public void inAArraycellStatement(AArraycellStatement node) {
        String varName = Utils.extractId(node.getIdentifier()).toString();
        Type type = Utils.extractType(node.getVal(), functions, vars, currentFunc);
        vars.put(varName, type);
    }

    @Override
    public void inAFunctionCall(AFunctionCall node) {
        currentFunc = new FunCall(Utils.extractId(node.getIdentifier()).toString());
    }

    @Override
    public void outAFunctionCall(AFunctionCall node) {
        FunctionArgs args = null;
        TId id = Utils.extractId(node.getIdentifier());
        if (!functions.containsKey(id.toString())) {
            System.err.println("Line " + id.getLine() + ": " + id.toString() + "is not defined");
        } else {
            for (FunctionArgs function : functions.get(id.toString())) {
                if (function.args.size() >= currentFunc.args.size() && Utils.getRequiredArgs(function) <= Utils.getRequiredArgs(currentFunc)) {
                    function.returnType = Utils.extractType(function.returnExpression, functions, vars, currentFunc);
                    args = function;
                }
            }
            System.err.println("No overloading functions found for " + currentFunc.name);
        }
        if (args != null) {
            String[] fTypes = args.args.keySet().toArray(new String[0]);
            for (int i = 0; i < currentFunc.args.size(); i++) {
                vars.put(fTypes[i], currentFunc.args.get(i));
            }
            args.returnType = Utils.extractType(args.returnExpression, functions, vars, currentFunc);
        }
    }

    @Override
    public void inAArrayExpression(AArrayExpression node) {
        PArrayExpression exp = node.getArrayExpression();
        PExpression expression;
        if(exp instanceof AManyArrayExpression){
            expression = ((AManyArrayExpression) exp).getExpression();
        }else{
            expression = ((ASingleArrayExpression) exp).getExpression();
        }
        if (expression instanceof AValueExpression) {
            ((AValueExpression) expression).getValue();
        } else if (expression instanceof AIdentifierExpression) {
            TId id = Utils.extractId(((AIdentifierExpression) expression).getIdentifier());
            if (!vars.containsKey(id.toString())) {
                int line = id.getLine();
                System.out.println("Error in line: " + line + ", variable " + id.toString() + " not defined");
            }
        }
    }

    @Override
    public void inAIdentifierExpression(AIdentifierExpression node) {
        TId id = Utils.extractId(node.getIdentifier());
        if (!vars.containsKey(id.toString())) {
            int line = id.getLine();
            System.out.println("Error in line: " + line + ", variable " + id.toString() + " not defined");
        }
    }

    //Check numeric operations
    @Override
    public void inAAdditionExpression(AAdditionExpression node) {
        PExpression left = node.getLpar();
        PExpression right = node.getRpar();
        getOperationType(left, right);
    }

    @Override
    public void inASubtractionExpression(ASubtractionExpression node) {
        PExpression left = node.getLpar();
        PExpression right = node.getRpar();
        getOperationType(left, right);
    }

    @Override
    public void inAMultiplicationExpression(AMultiplicationExpression node) {
        PExpression left = node.getLpar();
        PExpression right = node.getRpar();
        getOperationType(left, right);
    }

    @Override
    public void inADivisionExpression(ADivisionExpression node) {
        PExpression left = node.getLpar();
        PExpression right = node.getRpar();
        getOperationType(left, right);
    }

    @Override
    public void inAModuloExpression(AModuloExpression node) {
        PExpression left = node.getLpar();
        PExpression right = node.getRpar();
        getOperationType(left, right);
    }


    //Track current line
    @Override
    public void caseTId(TId node) {
        super.caseTId(node);
        currentLine = node.getLine();
    }

    @Override
    public void caseTNumber(TNumber node) {
        super.caseTNumber(node);
        currentLine = node.getLine();
    }

    @Override
    public void caseTNone(TNone node) {
        super.caseTNone(node);
        currentLine = node.getLine();
    }

    @Override
    public void caseTString(TString node) {
        super.caseTString(node);
        currentLine = node.getLine();
    }

    //Flag for when we are in a return statement
    @Override
    public void inAReturnStatement(AReturnStatement node) {
        returning = true;
    }

    @Override
    public void outAReturnStatement(AReturnStatement node) {
        returning = false;
    }

    private void getOperationType(PExpression operandA, PExpression operandB) {
        //If we are in a return statement ignore the check since we don't know what each variable might be
        if (!returning) {
            Type left, right;
            if (vars.containsKey(operandA.toString())) {
                left = vars.get(operandA.toString());
            } else {
                left = Utils.extractType(operandA, functions, vars, null);
            }

            //Get b Type
            if (vars.containsKey(operandB.toString())) {
                right = vars.get(operandB.toString());
            } else {
                right = Utils.extractType(operandB, functions, vars, null);
            }

            //check that operands are of the same type
            if ((left == Type.NUMBER && right == Type.NUMBER) || (left == Type.STRING && right == Type.STRING)) {
                vars.put(operandA.parent().toString(), left);
            }
            //Else print error and set it's type as Numeric to avoid further errors
            else {
                System.err.println("Line " + currentLine + ": Incompatible types: " + left.toString() + " and " + right.toString());
                vars.put(operandA.parent().toString(), Type.NUMBER);
            }
        }
    }

    class FunCall {
        ArrayList<Type> args;
        String name;

        FunCall(String name) {
            this.name = name;
            this.args = new ArrayList<>();
        }
    }
}
