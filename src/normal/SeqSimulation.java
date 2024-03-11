package normal;

import common.Simulation;
import common.Vector2;

public class SeqSimulation extends Simulation {

    public SeqSimulation(int numBodies, int simSteps) {
        // Run basic simulation initialization
        super(numBodies, simSteps);
    }

    // Calculate forces for all bodies present in simulation
    @Override
    protected void calculateForces() {
        double dist, mag;
        Vector2 dir;
        for (int i = 0; i < bodies.length - 1; i++) {
            for (int j = i + 1; j < bodies.length; j++) {

                dist = Vector2.dist(bodies[i].position, bodies[j].position);
                // Newtons gravitational law
                mag = (G_CONSTANT * bodies[i].mass * bodies[j].mass) / dist * dist;
                dir = Vector2.sub(bodies[j].position, bodies[i].position);

                // Updated both bodies forces
                bodies[i].force.x += mag * dir.x / dist;
                bodies[j].force.x -= mag * dir.x / dist;
                bodies[i].force.y += mag * dir.y / dist;
                bodies[j].force.y -= mag * dir.y / dist;
            }
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
    }
}
