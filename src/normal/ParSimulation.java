package normal;

import java.util.concurrent.*;

import common.*;

public class ParSimulation extends Simulation {

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

    private ThreadPoolExecutor executor;

    public CyclicBarrier barrier;
    public CyclicBarrier internalBarrier;

    private int threadCount = 12;

    public ParSimulation(int numBodies, int simSteps) {
        super(numBodies, simSteps);

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        internalBarrier = new CyclicBarrier(threadCount, null);
        barrier = new CyclicBarrier(threadCount + 1, null);
    }

    @Override
    protected void calculateForces() {
    }

    @Override
    protected void updatePositions() {
        for (int i = 0; i < threadCount; i++) {
            int start = i * (int) Math.floor(bodies.length / threadCount);
            int end = start + bodies.length / threadCount;
            if (i == threadCount - 1) {
                end = bodies.length;
            }
            executor.execute(new WorkerTask(start, end));
        }
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public class WorkerTask implements Runnable {

        int start;
        int end;

        public WorkerTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            try {
                // long startTime = System.nanoTime();
                calculateForces();
                internalBarrier.await();

                updatePositions();
                barrier.await();

                // long endTime = System.nanoTime();

                // double runTime = (endTime - startTime) / 1000000.0;
                // System.out.printf("Time: %f ms | FPS: %f\r", runTime, 1000.0 / runTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void calculateForces() {
            double dist, mag;
            Vector2 dir;
            for (int i = start; i < end; i++) {
                for (int j = i + 1; j < bodies.length; j++) {
                    Vector2 scaledVecI = bodies[i].position;
                    Vector2 scaledVecJ = bodies[j].position;

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

        private void updatePositions() {
            Vector2 deltaV;
            Vector2 deltaP;
            for (int i = start; i < end; i++) {

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
        }
    }
}
