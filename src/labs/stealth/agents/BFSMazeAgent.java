package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;


import java.util.HashSet;       // will need for bfs
import java.util.Queue;         // will need for bfs
import java.util.LinkedList;    // will need for bfs
import java.util.Set;           // will need for bfs


// JAVA PROJECT IMPORTS
import java.util.HashMap;      // will need for path reconstruction
import java.util.Stack;        // will need for path reconstruction


public class BFSMazeAgent
    extends MazeAgent
{

    public BFSMazeAgent(int playerNum)
    {
        super(playerNum);
    }

    // use bfs to find path from src to goal while avoiding the trees in the map, return the path as a Path object
    @Override
    public Path search(Vertex src,
                       Vertex goal,
                       StateView state)
    {
        // create a queue for bfs
        Queue<Vertex> queue = new LinkedList<Vertex>();

        // create a set to keep track of visited vertices
        Set<Vertex> visited = new HashSet<Vertex>();

        // create a hash map for path reconstruction. Key is vertex, value is parent vertex
        HashMap<Vertex, Vertex> path = new HashMap<Vertex, Vertex>();

        // add src to queue
        queue.add(src);
        visited.add(src);

        // while queue is not empty
        while (!queue.isEmpty())
        {
            // dequeue a vertex
            Vertex v = queue.remove();

            // if vertex is goal, return path
            if (v.equals(goal))
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
            neighbors.add(new Vertex(vx - 1, vy - 1));
            neighbors.add(new Vertex(vx + 1, vy - 1));
            neighbors.add(new Vertex(vx - 1, vy + 1));

            // for each neighbor
            for (Vertex neighbor : neighbors)
            {
                int neighborX = neighbor.getXCoordinate();
                int neighborY = neighbor.getYCoordinate();

                // if neighbor is not visited and is not a tree and it not out of bounds of map, and it is not occupied by a footman
                if (!visited.contains(neighbor) && !state.isResourceAt(neighborX, neighborY) && (state.unitAt(neighborX, neighborY) == null || state.unitAt(neighborX, neighborY) == 4)
                    && state.inBounds(neighborX, neighborY))
                {
                    // add neighbor to queue
                    queue.add(neighbor);
                    visited.add(neighbor);
                    path.put(neighbor, v);

                    if (neighbor.equals(goal))
                    {
                        queue.clear();
                        break;
                    }
                }
            }
        }

        // path reconstruction
        if (path.containsKey(goal))
        {
            // get parent node of goal, since we do not want to occupy the townhall
            Vertex parent = path.get(goal);
            Stack <Vertex> stack = new Stack<Vertex>();
            stack.push(parent);

            // while parent is not src, add parent to path
            while (!parent.equals(src))
            {
                parent = path.get(parent);
                stack.push(parent);
            }

            // create a path object
            Path p = new Path(stack.pop());

            // pop the stack to get it in reverse order
            while (!stack.isEmpty())
            {
                p = new Path(stack.pop(), 1f, p);
            }

            System.out.println(p);
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
