package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;


import java.util.HashSet;   // will need for dfs
import java.util.Stack;     // will need for dfs
import java.util.Set;       // will need for dfs
import java.util.LinkedList;
import java.util.Queue;
// JAVA PROJECT IMPORTS
import java.util.HashMap;      // will need for path reconstruction


public class DFSMazeAgent
    extends MazeAgent
{

    public DFSMazeAgent(int playerNum)
    {
        super(playerNum);
    }

    @Override
    public Path search(Vertex src,
                       Vertex goal,
                       StateView state)
    {
        // get x and y coordinat of src and goal
        int goalX = goal.getXCoordinate();
        int goalY = goal.getYCoordinate();

        Stack<Vertex> stack = new Stack<Vertex>();

        // create a set to keep track of visited vertices
        Set<Vertex> visited = new HashSet<Vertex>();

        //  create a hash map for path reconstruction. Key is vertex, value is parent vertex
        HashMap<Vertex, Vertex> path = new HashMap<Vertex, Vertex>();

        stack.add(src);
        visited.add(src);

        while (!stack.isEmpty())
        {
            // pop the stack a vertex
            Vertex v = stack.pop();

            // if vertex is goal, return path
            if (v.getXCoordinate() == goalX && v.getYCoordinate() == goalY)
            {
                break;
            }

            // get neighbors of vertex, by getting up down left and right vertices
            Set<Vertex> neighbors = new HashSet<>();
            int vx = v.getXCoordinate();
            int vy = v.getYCoordinate();
            neighbors.add(new Vertex(vx + 1, vy));
            neighbors.add(new Vertex(vx - 1, vy));
            neighbors.add(new Vertex(vx, vy + 1));
            neighbors.add(new Vertex(vx, vy - 1));

            // include diagonal neighbors
            neighbors.add(new Vertex(vx + 1, vy + 1));
            neighbors.add(new Vertex(vx + 1, vy - 1));
            neighbors.add(new Vertex(vx - 1, vy + 1));
            neighbors.add(new Vertex(vx - 1, vy - 1));

            // for each neighbor
            for (Vertex neighbor : neighbors)
            {
                int neighborX = neighbor.getXCoordinate();
                int neighborY = neighbor.getYCoordinate();

                // System.out.println("neighbor: " + neighborX + ", " + neighborY);
                // System.out.println(state.unitAt(neighborX, neighborY));

                // if neighbor is not visited and is not a tree and it not out of bounds of map, and it is not occupied by a footman
                if (!visited.contains(neighbor) && !state.isResourceAt(neighborX, neighborY) && (state.unitAt(neighborX, neighborY) == null || state.unitAt(neighborX, neighborY) == 4)
                    && state.inBounds(neighborX, neighborY))
                {
                    // add neighbor to queue
                    stack.add(neighbor);
                    visited.add(neighbor);
                    // check if path already contains neighbor, if it does then do not update path
                    if (!path.containsKey(neighbor))
                    {
                    path.put(neighbor, v);
                    }
                }
            }
        }

        // path reconstruction
        if (path.containsKey(goal))
        {
            // get parent node of goal, since we do not want to occupy the townhall
            Vertex parent = path.get(goal);
            Stack <Vertex> reverse_stack = new Stack<Vertex>();
            reverse_stack.push(parent);

            // while parent is not src, add parent to path
            while (!parent.equals(src))
            {
                parent = path.get(parent);
                reverse_stack.push(parent);
            }

            // create a path object
            Path p = new Path(reverse_stack.pop());

            // pop the stack to get it in reverse order
            while (!reverse_stack.isEmpty())
            {
                p = new Path(reverse_stack.pop(), 1f, p);
            }

            // System.out.println(p);
            return p;

        } else
        {
            // System.out.println("No path found");
            return null;
        }

    }

    @Override
    public boolean shouldReplacePlan(StateView state)
    {
        // loop through the path and check if any of the vertices are occupied any other unit other than player unit
        Stack <Vertex> stack = this.getCurrentPlan();

        // traverse the stack wihtout popping 
        for (int i = 0; i < stack.size(); i++)
        {
            Vertex v = stack.get(i);
            int vx = v.getXCoordinate();
            int vy = v.getYCoordinate();

            // if vertex is occupied by a unit other than player unit, return true
            if (state.unitAt(vx, vy) != null && state.unitAt(vx, vy) != 4)
            {
                return true;
            }
        }
        return false;
    }

}
