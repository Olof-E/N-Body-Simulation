package barnesHut;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import common.Simulation;
import common.Vector2;

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

    public CountDownLatch latch;

    private int threadCount = 4;

    public ParSimulation(int numBodies, int simSteps) {
        super(numBodies, simSteps);

        quadTree = new QuadTree(new Vector2(SIM_RADIUS, SIM_RADIUS));
        for (int i = 0; i < bodies.length; i++) {
            quadTree.Insert(bodies[i]);
        }

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        internalBarrier = new CyclicBarrier(threadCount, null);
        barrier = new CyclicBarrier(threadCount + 1, null);
    }

    @Override
    protected void calculateForces() {
    }

    @Override
    protected void updatePositions() {
        quadTree.ComputePseudoBodies();

        for (int i = 0; i < threadCount; i++) {
            int start = i * (int) Math.floor(bodies.length / threadCount);
            int end = start + bodies.length / threadCount;
            if (i == threadCount - 1) {
                end = bodies.length;
            }
            executor.submit(new WorkerTask(start, end));
        }
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        quadTree.Reset();
        for (int j = 0; j < bodies.length; j++) {
            quadTree.Insert(bodies[j]);
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
                calculateForces();
                internalBarrier.await();

                updatePositions();

                barrier.await();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void calculateForces() {

            for (int i = start; i < end; i++) {
                bodies[i].force = quadTree.calculateForce(bodies[i]);
            }
        }

        private void updatePositions() {
            Vector2 deltaV;
            Vector2 deltaP;
            for (int i = start; i < end; i++) {

                deltaV = Vector2.div(bodies[i].force, bodies[i].mass / DT);
                deltaP = Vector2.mul(Vector2.add(bodies[i].velocity, Vector2.div(deltaV, 2)), DT);

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
