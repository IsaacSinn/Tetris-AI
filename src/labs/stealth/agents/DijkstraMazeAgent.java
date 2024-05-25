package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.util.Direction;                           // Directions in Sepia


import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue; // heap in java
import java.util.Set;
import java.util.Stack;


// JAVA PROJECT IMPORTS
import java.lang.Math;

public class DijkstraMazeAgent
    extends MazeAgent
{

    public DijkstraMazeAgent(int playerNum)
    {
        super(playerNum);
    }

    @Override
    public Path search(Vertex src,
                       Vertex goal,
                       StateView state)
    {
        // hash map for distances with float
        Map<Vertex, Float> distances = new HashMap<Vertex, Float>();

        // loop through all vertex and initialize all distances to infinity
        for (int i = 0; i < state.getXExtent(); i++)
        {
            for (int j = 0; j < state.getYExtent(); j++)
            {
                Vertex v = new Vertex(i, j);
                distances.put(v, Float.MAX_VALUE);
            }
        }

        //src distance is 0
        distances.put(src, 0f);

		// priority queue for dijkstra with comaparator
        PriorityQueue<Vertex> pq = new PriorityQueue<Vertex>(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex v1, Vertex v2) {
                if (distances.get(v1) < distances.get(v2)) {
                    return -1;
                } else if (distances.get(v1) > distances.get(v2)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        // create a set to keep track of visited vertices
        Set<Vertex> visited = new HashSet<Vertex>();

        // create a hash map for path reconstruction. Key is vertex, value is parent vertex
        HashMap<Vertex, Vertex> path = new HashMap<Vertex, Vertex>();

        // add src to queue
        pq.add(src);
        visited.add(src);

        // while queue is not empty
        while (!pq.isEmpty())
        {
            Vertex v = pq.remove();
            Float current_distance = distances.get(v);

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

            for (Vertex neighbor : neighbors)
            {
                int neighborX = neighbor.getXCoordinate();
                int neighborY = neighbor.getYCoordinate();

                // if neighbor is not visited and is not a tree and it not out of bounds of map, and it is not occupied by a footman
                if (!visited.contains(neighbor) && !state.isResourceAt(neighborX, neighborY) && (state.unitAt(neighborX, neighborY) == null || state.unitAt(neighborX, neighborY) == 4)
                    && state.inBounds(neighborX, neighborY))
                {
                    float new_distance;

                    // update the new_distance based on the direction
                    if (neighborX == vx + 1 && neighborY == vy)
                    {
                        new_distance = current_distance + 5;
                    }
                    else if (neighborX == vx - 1 && neighborY == vy)
                    {
                        new_distance = current_distance + 5;
                    }
                    else if (neighborX == vx && neighborY == vy + 1)
                    {
                        new_distance = current_distance + 1;
                    }
                    else if (neighborX == vx && neighborY == vy - 1)
                    {
                        new_distance = current_distance + 10;
                    }
                    else if (neighborX == vx + 1 && neighborY == vy + 1)
                    {
                        new_distance = current_distance + (float) Math.sqrt(5*5 + 1);
                    }
                    else if (neighborX == vx - 1 && neighborY == vy - 1)
                    {
                        new_distance = current_distance + (float) Math.sqrt(5*5 + 10*10);
                    }
                    else if (neighborX == vx + 1 && neighborY == vy - 1)
                    {
                        new_distance = current_distance + (float) Math.sqrt(5*5 + 10*10);
                    }
                    else if (neighborX == vx - 1 && neighborY == vy + 1)
                    {
                        new_distance = current_distance + (float) Math.sqrt(5*5 + 1);
                    }
                    else
                    {
                        new_distance = current_distance;
                    }

                    if (new_distance < distances.get(neighbor))
                    {
                        distances.put(neighbor, new_distance);
                        pq.add(neighbor);
                        
                    }
                    visited.add(neighbor);
                    path.put(neighbor, v);

                    // if (neighbor.equals(goal))
                    // {
                    //     pq.clear();
                    //     break;
                    // }
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
            
            Vertex v = stack.pop();

            // create a path object
            Path p = new Path(v);

            // pop the stack to get it in reverse order
            while (!stack.isEmpty())
            {
                Vertex next = stack.pop();

                // update the path with next vertex and the weight depend on the direction
                p = new Path(next, distances.get(next) - distances.get(path.get(next)), p);
                // System.out.println(next);
                // System.out.println(p);
                // System.out.println(distances.get(next) - distances.get(path.get(next)));
            }

            System.out.println(p);
            return p;

        } else
        {
            System.out.println("No path found");
            return null;
        }
    }

    @Override
    public boolean shouldReplacePlan(StateView state)
    {
        return false;
    }

}
