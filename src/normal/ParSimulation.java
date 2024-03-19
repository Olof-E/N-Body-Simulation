package normal;

import java.util.concurrent.*;

import common.*;

public class ParSimulation extends Simulation {

    public CyclicBarrier barrier;
    public CyclicBarrier internalBarrier;

    public boolean finished = false;

    private Thread[] workers;
    private int threadCount;

    public ParSimulation(int numBodies, int simSteps, int threadCount) {
        // Run basic simulation initialization
        super(numBodies, simSteps);

        // Initialize barriers and threads
        this.threadCount = threadCount;
        workers = new Thread[threadCount];

        internalBarrier = new CyclicBarrier(threadCount, null);
        barrier = new CyclicBarrier(threadCount + 1, null);

        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Thread(new WorkerTask(i));
            workers[i].start();

        }
    }

    @Override
    public void Run() {
        super.Run();
        // Signal threads that they can terminate
        finished = true;
        for (int i = 0; i < workers.length; i++) {
            workers[i].interrupt();
        }
    }

    @Override
    protected void calculateForces() {
    }

    @Override
    protected void updatePositions() {
        try {
            // Wait for all threads to finish before proceeding to next time step
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    // Code to run in parallel
    public class WorkerTask implements Runnable {

        int id;

        public WorkerTask(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {

                    calculateForces();

                    // Sync with other threads
                    internalBarrier.await();

                    updatePositions();

                    // Sync with main loop
                    barrier.await();

                }
            } catch (Exception e) {
                return;
            }
        }

        // Calculate forces for all bodies present in simulation
        private void calculateForces() {
            double dist, mag;
            Vector2 dir;
            for (int i = id; i < bodies.length; i += threadCount) {
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
        private void updatePositions() {
            Vector2 deltaV;
            Vector2 deltaP;
            for (int i = id; i < bodies.length; i += threadCount) {
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

}
