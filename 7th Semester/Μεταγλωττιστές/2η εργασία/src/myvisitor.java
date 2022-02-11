import minipython.analysis.*;
import minipython.node.*;

import java.util.*;

/* Visitor used for a first pass of the example file.
Stores the function definitions encountered, as well as the variables declared for later
analysis on the second pass */
public class myvisitor extends DepthFirstAdapter {

    /* HashTable used to store function definitions.
     * Functions can be overloaded (same name, but different parameter list)
     * Therefore for each function with the same name we encounter, we store its parameters in the list
     */
    private Hashtable<String, List<FunctionArgs>> functions; //table used for function definitions
    private Hashtable<String, Type> vars;

    myvisitor(Hashtable functions, Hashtable vars) {
        this.functions = functions;
        this.vars = vars;
    }

    private FunctionArgs temp; //keep the most recently found function definition

    @Override
    public void inAFunction(AFunction node) {
        System.out.println("In a function");
        String fName = Utils.extractId(node.getIdentifier()).toString();
        temp = new FunctionArgs(fName);
        int line = Utils.extractId(node.getIdentifier()).getLine();
        //if function is already defined, check if it overloaded
        if (functions.containsKey(fName)) {
            List<FunctionArgs> fnames = functions.get(fName);
            //if overloaded function has the same number of arguments, then we have a redefinition error
            for (FunctionArgs args : fnames) {
                if (args.getArgs().size() == temp.getArgs().size()) {
                    System.out.println("Line " + line + ": " + " Function " + fName + " is already defined");
                    return;
                }
            }
            //add overloaded function to the list of functions by the same name
            functions.get(fName).add(temp);
        } else {
            //function is not defined. Add its definition to the table
            List<FunctionArgs> newArgs = new ArrayList<>();
            newArgs.add(temp);
            functions.put(fName, newArgs);
        }
    }

    /*Every node after a function definition is either a SingleArgument (argument with no default value)
    or a SingleAssignArgument (argument with default value). In the second case we evaluate its type
     */

    @Override
    public void inASingleArgument(ASingleArgument node)
    {
        TId id = Utils.extractId(node.getIdentifier());
        temp.args.put(id.toString(), Type.NONE);
        vars.put(id.toString(), Type.NONE);
    }

    public void inASingleAssignArgument(ASingleAssignArgument node)
    {
        TId id = Utils.extractId(node.getIdentifier());
        Type type = Utils.findVariableType(node.getValue());
        temp.args.put(id.toString(), type);
        vars.put(id.toString(), type);
    }

}
