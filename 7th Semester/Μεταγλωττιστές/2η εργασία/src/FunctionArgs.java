import minipython.node.PExpression;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/* Helper class used to store function arguments.
 */
public class FunctionArgs {

    Hashtable<String, Type> args;
    String name;
    public Type returnType;
    public PExpression returnExpression = null;

    public FunctionArgs(String name){
        returnType = Type.NONE;
        args = new Hashtable<>();
        this.name = name;
    }

    public FunctionArgs(Hashtable<String, Type> args) {
        this.args = args;
    }

    public FunctionArgs() {
    }

    public Hashtable<String, Type> getArgs() {
        return args;
    }

    public void setArgs(Hashtable<String, Type> args) {
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Function Name: ").append(name).append("\n");
        for(Map.Entry<String, Type> arg: args.entrySet()){
            builder.append(arg.getKey()).append(": ").append(arg.getValue()).append("\n");
        }
        return builder.toString();
    }
}
