package barnesHut;

import common.Simulation;
import common.Vector2;

public class SeqSimulation extends Simulation {

    public SeqSimulation(int numBodies, int simSteps) {
        // Run basic simulation initialization
        super(numBodies, simSteps);

        // Initialize barnes-hut tree
        quadTree = new QuadTree(new Vector2(SIM_RADIUS, SIM_RADIUS));
        for (int i = 0; i < bodies.length; i++) {
            quadTree.Insert(bodies[i]);
        }
    }

    // Calculate forces acting on each body
    @Override
    protected void calculateForces() {
        quadTree.ComputePseudoBodies();
        for (int i = 0; i < bodies.length; i++) {
            bodies[i].force = quadTree.calculateForce(bodies[i]);
        }
    }

    // Calculate positions & velocities for all bodies
    @Override
    protected void updatePositions() {
        Vector2 deltaV;
        Vector2 deltaP;
        for (int i = 0; i < bodies.length; i++) {
            // Change in velocity based on current force
            deltaV = Vector2.div(bodies[i].force, bodies[i].mass / DT);

            // Change in position based on current velocity
            deltaP = Vector2.mul(Vector2.add(bodies[i].velocity, Vector2.div(deltaV, 2)), DT);

            // Update velocities and positions
            bodies[i].velocity.x += deltaV.x;
            bodies[i].velocity.y += deltaV.y;
            bodies[i].position.x += deltaP.x;
            bodies[i].position.y += deltaP.y;
            bodies[i].force = new Vector2();

            // Collision check with simulation bounds
            if (bodies[i].position.x <= 0) {
                bodies[i].position.x = 1;
                bodies[i].velocity.x = -bodies[i].velocity.x / 2;
            } else if (bodies[i].position.x >= SIM_RADIUS) {
                bodies[i].position.x = SIM_RADIUS - 1;
                bodies[i].velocity.x = -bodies[i].velocity.x / 2;
            }

            if (bodies[i].position.y <= 0) {
                bodies[i].position.y = 1;
                bodies[i].velocity.y = -bodies[i].velocity.y / 2;
            } else if (bodies[i].position.y >= SIM_RADIUS) {
                bodies[i].position.y = SIM_RADIUS - 1;
                bodies[i].velocity.y = -bodies[i].velocity.y / 2;
            }
        }

        // Update barnes-hut tree with new info
        quadTree.Reset();
        for (int i = 0; i < bodies.length; i++) {
            quadTree.Insert(bodies[i]);
        }
    }
}
