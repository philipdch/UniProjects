import minipython.node.*;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Utils {

    static TId extractId(PIdentifier identifier) {
        return ((AIdentifier) identifier).getId();
    }

    //find value of expression
    static Type extractType(PExpression expression, Hashtable<String, List<FunctionArgs>> functions, Hashtable<String, Type> vars, SecondVisitor.FunCall currentFun) {
        if (expression instanceof AValueExpression) {
            return findVariableType(((AValueExpression) expression).getValue());
        } else if (expression instanceof AIdentifierExpression) {
            String id = extractId(((AIdentifierExpression) expression).getIdentifier()).toString();
            return (vars.contains(id)) ? vars.get(id) : Type.NONE;
        } else if (expression instanceof AFunctioncallExpression && currentFun != null) {
            TId id = extractId(((AFunctionCall) ((AFunctioncallExpression) expression).getFunctionCall()).getIdentifier());
            String functionName = id.toString();
            if (functions.containsKey(functionName)) {
                //Check overloaded functions
                FunctionArgs foundFun = null;
                for (FunctionArgs funtion : functions.get(functionName)) {
                    //If function was found, return its type
                    if (funtion.args.size() >= currentFun.args.size() && getRequiredArgs(funtion) <= getRequiredArgs(currentFun)) {
                        foundFun = funtion;
                        break;
                    }
                }
                System.err.println("Line " + id.getLine() + ": No overload function found for " + currentFun.name);
                return (foundFun == null) ? Type.FUNCTION : Type.NUMBER;
            }
            return Type.NONE;
        }else if(expression instanceof AMaxExpression || expression instanceof AMinExpression || expression instanceof ALenExpression) {
            return Type.NUMBER;
        }else if(expression instanceof AListExpressionExpression){
            TId id = extractId(((AListExpressionExpression) expression).getIdentifier());
            return (vars.contains(id.toString())) ? vars.get(id.toString()) : Type.NONE;
        }else{
            return Type.NUMBER;
        }
    }

    static Type findVariableType(PValue value) {
        if (value instanceof AFuncCallValue)
            return Type.FUNCTION;
        else if (value instanceof ANegNumValue)
            return Type.NUMBER;
        else if (value instanceof APosNumValue)
            return Type.NUMBER;
        else if (value instanceof AStringValue)
            return Type.STRING;
        else
            return Type.NONE;
    }

    static int getRequiredArgs(FunctionArgs args) {
        int requiredArgs = 0;

        //if argument type is NONE, then it has no default value and thus is required
        for (Map.Entry<String, Type> arg : args.args.entrySet()) {
            if (arg.getValue() == Type.NONE) requiredArgs++;
        }
        return requiredArgs;
    }

    static int getRequiredArgs(SecondVisitor.FunCall args) {
        int requiredArgs = 0;

        //if argument type is NONE, then it has no default value and thus is required
        for (Type arg : args.args) {
            if (arg == Type.NONE) requiredArgs++;
        }
        return requiredArgs;
    }



}
