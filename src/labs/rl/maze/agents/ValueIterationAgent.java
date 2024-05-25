package src.labs.rl.maze.agents;


// SYSTEM IMPORTS
import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.util.Direction;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


// JAVA PROJECT IMPORTS
import edu.bu.labs.rl.maze.agents.StochasticAgent;
import edu.bu.labs.rl.maze.agents.StochasticAgent.RewardFunction;
import edu.bu.labs.rl.maze.agents.StochasticAgent.TransitionModel;
import edu.bu.labs.rl.maze.utilities.Coordinate;
import edu.bu.labs.rl.maze.utilities.Pair;



public class ValueIterationAgent
    extends StochasticAgent
{

    public static final double GAMMA = 1.0; // feel free to change this around!
    public static final double EPSILON = 1e-6; // don't change this though

    private Map<Coordinate, Double> utilities;

	public ValueIterationAgent(int playerNum)
	{
		super(playerNum);
        this.utilities = null;
	}

    public Map<Coordinate, Double> getUtilities() { return this.utilities; }
    private void setUtilities(Map<Coordinate, Double> u) { this.utilities = u; }

    public boolean isTerminalState(Coordinate c)
    {
        return c.equals(StochasticAgent.POSITIVE_TERMINAL_STATE)
            || c.equals(StochasticAgent.NEGATIVE_TERMINAL_STATE);
    }

    /**
     * A method to get an initial utility map where every coordinate is mapped to the utility 0.0
     */
    public Map<Coordinate, Double> getZeroMap(StateView state)
    {
        Map<Coordinate, Double> m = new HashMap<Coordinate, Double>();
        for(int x = 0; x < state.getXExtent(); ++x)
        {
            for(int y = 0; y < state.getYExtent(); ++y)
            {
                if(!state.isResourceAt(x, y))
                {
                    // we can go here
                    m.put(new Coordinate(x, y), 0.0);
                }
            }
        }
        return m;
    }

    // calculates the utility of a state using the bellman equation and value iteration algorithm
    public void valueIteration(StateView state)
    {
        // initialize the utilities
        Map<Coordinate, Double> utilities = this.getZeroMap(state);
        Map<Coordinate, Double> newUtilities = new HashMap<Coordinate, Double>();

        // set the utilities of the terminal states
        for (Coordinate c: utilities.keySet()) {
            if (isTerminalState(c)) {
                utilities.put(c, RewardFunction.getReward(c));
            }
        }

        System.out.println("Initial utilities: " + utilities);

        // keep track of the change in utilities
        double delta = Double.POSITIVE_INFINITY;

        while (delta > (EPSILON * (1- GAMMA)) / GAMMA) {
            delta = 0.0;
            
            for (Coordinate c: utilities.keySet()) {
                newUtilities.put(c, utilities.get(c));
            }

            // iterate over all states
            for (Coordinate c: utilities.keySet()) {
                if (isTerminalState(c)) {
                    continue;
                }

                // find the best action for the state
                double maxActionUtility = Double.NEGATIVE_INFINITY;
                for (Direction d: TransitionModel.CARDINAL_DIRECTIONS) {
                    double thisActionUtility = 0.0;
                    for (Pair<Coordinate, Double> transition: TransitionModel.getTransitionProbs(state, c, d)) {
                        thisActionUtility += transition.getSecond() * utilities.get(transition.getFirst());
                    }

                    if (thisActionUtility > maxActionUtility) {
                        maxActionUtility = thisActionUtility;
                    }
                }

                // update the utility of the state
                newUtilities.put(c, RewardFunction.getReward(c) + GAMMA * maxActionUtility);

                // update the change in utilities
                delta = Math.max(delta, Math.abs(newUtilities.get(c) - utilities.get(c)));
            }

            this.setUtilities(utilities);

            // update the utilities
            for(Coordinate c : newUtilities.keySet())
            {
                utilities.put(c, newUtilities.get(c));
            }

            System.out.println("Utilities: " + utilities);
        }

    }

    @Override
    public void computePolicy(StateView state,
                              HistoryView history)
    {
        // compute the utilities
        this.valueIteration(state);

        // compute the policy from the utilities
        Map<Coordinate, Direction> policy = new HashMap<Coordinate, Direction>();

        for(Coordinate c : this.getUtilities().keySet())
        {
            // figure out what to do when in this state
            double maxActionUtility = Double.NEGATIVE_INFINITY;
            Direction bestDirection = null;

            // go over every action
            for(Direction d : TransitionModel.CARDINAL_DIRECTIONS)
            {

                // measure how good this action is as a weighted combination of future state's utilities
                double thisActionUtility = 0.0;
                for(Pair<Coordinate, Double> transition : TransitionModel.getTransitionProbs(state, c, d))
                {
                    thisActionUtility += transition.getSecond() * this.getUtilities().get(transition.getFirst());
                }

                // keep the best one!
                if(thisActionUtility > maxActionUtility)
                {
                    maxActionUtility = thisActionUtility;
                    bestDirection = d;
                }
            }

            // policy recommends the best action for every state
            policy.put(c, bestDirection);
        }

        this.setPolicy(policy);
    }

}
