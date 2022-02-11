import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/* Class implementing the A* algorithm using a Closed Set */
public class AStar {

    private List<State> states = null;
    private HashSet<State> closedSet = null;

    public AStar(){
    }

    /*implementation of the algorithm. Given an initial state, calculates its children and adds them to the Search Frontier (states list)
      Then sorts the list based on the states' score (calculated by the heuristic function in State) in ascending order.
      After that the algorithm returns to the start, picks the first state in the list, checks if its a terminal state and if it has been already found in a previous iteration through the closed set.
      If not, it adds it to the Closed Set, calculates its children and adds them to the Search Frontier and sorts it again.
      Then the process repeats again.
      Note: If an upper bound is given, then the algorithm also checks if the state's total elapsed time exceeds this bound. If yes, the algorithm returns to the start without expanding the State
     */

    public State AStarAlgorithm(State initialState) {
        this.states = new ArrayList<State>();
        this.closedSet = new HashSet<State>();
        this.states.add(initialState);
        while(this.states.size() > 0)
        {
            State currentState = this.states.remove(0);
            if(currentState.isTerminal())
            {
                return currentState;
            }
            if(State.getTimeUpperBound() != -1 && currentState.getTotalTime() > State.getTimeUpperBound()) {
                this.closedSet.add(currentState);
                continue;
            }
            if(!closedSet.contains(currentState))
            {
                this.closedSet.add(currentState);
                this.states.addAll(currentState.getChildren());
                Collections.sort(this.states);
            }
        }
        return null;
    }
}
