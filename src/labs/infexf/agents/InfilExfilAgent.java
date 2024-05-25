package src.labs.infexf.agents;

// SYSTEM IMPORTS
import edu.bu.labs.infexf.agents.SpecOpsAgent;
import edu.bu.labs.infexf.distance.DistanceMetric;
import edu.bu.labs.infexf.graph.Vertex;
import edu.bu.labs.infexf.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;


// JAVA PROJECT IMPORTS
import java.util.Set;
import java.util.Stack;

public class InfilExfilAgent
    extends SpecOpsAgent
{

    public InfilExfilAgent(int playerNum)
    {
        super(playerNum);
    }

    // if you want to get attack-radius of an enemy, you can do so through the enemy unit's UnitView
    // Every unit is constructed from an xml schema for that unit's type.
    // We can lookup the "range" of the unit using the following line of code (assuming we know the id):
    //     int attackRadius = state.getUnit(enemyUnitID).getTemplateView().getRange();
    @Override
    public float getEdgeWeight(Vertex src,
                               Vertex dst,
                               StateView state)
    {
        // get all enemy unit IDs
        Set<Integer> enemyUnitIDs = this.getOtherEnemyUnitIDs();
        
        // get coordinates of dst
        int dstX = dst.getXCoordinate();
        int dstY = dst.getYCoordinate();

        float edgeWeight = 1f;
        // check if dst is within attack radius of an enemy, if so return infinity
        for(Integer unitID : enemyUnitIDs) {
            int attackRadius = state.getUnit(unitID).getTemplateView().getRange();

            int enemyX = state.getUnit(unitID).getXPosition();
            int enemyY = state.getUnit(unitID).getYPosition();

            if (Math.abs(enemyX - dstX) <= attackRadius && Math.abs(enemyY - dstY) <= attackRadius) {
                // System.out.println("src to dst: " + src + " to " + dst + " is infinity");
                return Float.POSITIVE_INFINITY;
            } else if (Math.abs(enemyX - dstX) <= attackRadius + 1 && Math.abs(enemyY - dstY) <= attackRadius + 1) {
                // System.out.println("src to dst: " + src + " to " + dst + " is 1");
                edgeWeight *= 100f;
            } else if (Math.abs(enemyX - dstX) <= attackRadius + 2 && Math.abs(enemyY - dstY) <= attackRadius + 2) {
                // System.out.println("src to dst: " + src + " to " + dst + " is 2");
                edgeWeight *= 50f;
            } else if (Math.abs(enemyX - dstX) <= attackRadius + 3 && Math.abs(enemyY - dstY) <= attackRadius + 3) {
                // System.out.println("src to dst: " + src + " to " + dst + " is 3");
                edgeWeight *= 20f;
            } else if (Math.abs(enemyX - dstX) <= attackRadius + 4 && Math.abs(enemyY - dstY) <= attackRadius + 4) {
                // System.out.println("src to dst: " + src + " to " + dst + " is 4");
                edgeWeight *= 5f;
            } else if (Math.abs(enemyX - dstX) <= attackRadius + 5 && Math.abs(enemyY - dstY) <= attackRadius + 5) {
                // System.out.println("src to dst: " + src + " to " + dst + " is 5");
                edgeWeight *= 2f;
            } else if (Math.abs(enemyX - dstX) <= attackRadius + 6 && Math.abs(enemyY - dstY) <= attackRadius + 6) {
                // System.out.println("src to dst: " + src + " to " + dst + " is 6");
                edgeWeight *= 1.5f;
            }
        }
        return edgeWeight;
    }

    @Override
    public boolean shouldReplacePlan(StateView state)
    {
        Set<Integer> enemyUnitIDs = this.getOtherEnemyUnitIDs();
        Stack<Vertex> plan = this.getCurrentPlan();

        System.out.println(plan);


        for (int i = 0; i < plan.size(); i++)
        {
            Vertex v = plan.get(i);
            int vx = v.getXCoordinate();
            int vy = v.getYCoordinate();

            // if vertex is occupied by a unit other than player unit, return true
            if (state.unitAt(vx, vy) != null && state.unitAt(vx, vy) != 4)
            {
                // System.err.println("Should replace plan: vertex occupied");
                return true;
            }

            for(Integer unitID : enemyUnitIDs) {
                if (state.getUnit(unitID) == null) {
                    continue;
                }

                int attackRadius = state.getUnit(unitID).getTemplateView().getRange() + 2;

                int enemyX = state.getUnit(unitID).getXPosition();
                int enemyY = state.getUnit(unitID).getYPosition();

                if (Math.abs(enemyX - vx) <= attackRadius && Math.abs(enemyY - vy) <= attackRadius) {
                    // System.err.println("Should replace plan: path within enemy range: " + vx + " " + vy);
                    return true;
                }
            }
        }
        return false;
    }

}
