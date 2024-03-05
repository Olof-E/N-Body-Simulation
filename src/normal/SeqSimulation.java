package normal;

import common.Simulation;
import common.Vector2;

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

    public SeqSimulation(int numBodies, int simSteps) {
        super(numBodies, simSteps);
    }

    private Vector2 scaleUp(Vector2 vec) {
        return new Vector2((vec.x / 1280) * SIM_RADIUS, (vec.y / 1280) * SIM_RADIUS);
    }

    @Override
    protected void calculateForces() {
        double dist, mag;
        Vector2 dir;
        for (int i = 0; i < bodies.length - 1; i++) {
            for (int j = i + 1; j < bodies.length; j++) {
                Vector2 scaledVecI = scaleUp(bodies[i].position);
                Vector2 scaledVecJ = scaleUp(bodies[j].position);

                dist = Vector2.dist(scaledVecI, scaledVecJ);
                mag = (G_CONSTANT * bodies[i].mass * bodies[j].mass) / dist * dist;
                dir = Vector2.sub(scaledVecJ, scaledVecI);

                bodies[i].force.x += mag * dir.x / dist;
                bodies[j].force.x -= mag * dir.x / dist;
                bodies[i].force.y += mag * dir.y / dist;
                bodies[j].force.y -= mag * dir.y / dist;
            }
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
            bodies[i].position.x += deltaP.x / SIM_RADIUS;
            bodies[i].position.y += deltaP.y / SIM_RADIUS;
            bodies[i].force = new Vector2();

            if (bodies[i].position.x <= 0) {
                bodies[i].position.x = 1;
                bodies[i].velocity.x = 0;
            } else if (bodies[i].position.x >= 1280) {
                bodies[i].position.x = 1279;
                bodies[i].velocity.x = 0;
            }

            if (bodies[i].position.y <= 0) {
                bodies[i].position.y = 1;
                bodies[i].velocity.y = 0;
            } else if (bodies[i].position.y >= 1280) {
                bodies[i].position.y = 1279;
                bodies[i].velocity.y = 0;
            }
        }
    }
}
