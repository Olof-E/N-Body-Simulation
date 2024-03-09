package barnesHut;

import common.Simulation;
import common.Vector2;
import common.Window;

public class SeqSimulation extends Simulation {

    // Test Simulation, basic solar system
    /*
     * bodies[0].mass = 3e30;
     * bodies[0].position = new Vector2(1280 / 2, 1280 / 2);
     * 
     * bodies[1].mass = ????
     * bodies[1].position = new Vector2(1280 / 2, 500);
     * bodies[1].velocity = new Vector2(8e19, 15e15);
     * 
     * bodies[2].mass = ???
     * bodies[2].position = new Vector2(1280 / 2, 1280 - 250);
     * bodies[2].velocity = new Vector2(-8e19, -15e15);
     */

    public QuadTree quadTree;

    public SeqSimulation(int numBodies, int simSteps) {
        super(numBodies, simSteps);

        quadTree = new QuadTree(new Vector2(SIM_RADIUS, SIM_RADIUS));
        for (int i = 0; i < bodies.length; i++) {
            quadTree.Insert(bodies[i]);
        }
    }

    @Override
    protected void calculateForces() {
        quadTree.ComputePseudoBodies();
        for (int i = 0; i < bodies.length; i++) {
            bodies[i].force = quadTree.calculateForce(bodies[i]);
        }
    }

    @Override
    protected void updatePositions() {
        Vector2 deltaV;
        Vector2 deltaP;
        for (int i = 0; i < bodies.length; i++) {

            deltaV = new Vector2(bodies[i].force.x / bodies[i].mass * DT, bodies[i].force.y / bodies[i].mass * DT);
            deltaP = new Vector2(
                    (bodies[i].velocity.x + deltaV.x / 2) * DT,
                    (bodies[i].velocity.y + deltaV.y / 2) * DT);

            bodies[i].velocity.x += deltaV.x;
            bodies[i].velocity.y += deltaV.y;
            bodies[i].position.x += deltaP.x;
            bodies[i].position.y += deltaP.y;
            bodies[i].force = new Vector2();

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
        quadTree.Reset();
        for (int i = 0; i < bodies.length; i++) {
            quadTree.Insert(bodies[i]);
        }
    }
}
