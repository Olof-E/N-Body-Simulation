package barnesHut;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import common.Simulation;
import common.Vector2;
import common.Window;

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

    public AtomicBoolean finished;

    private Thread[] workers;
    private int threadCount;

    public ParSimulation(int numBodies, int simSteps, int threadCount) {
        super(numBodies, simSteps);
        this.threadCount = threadCount;

        quadTree = new QuadTree(new Vector2(SIM_RADIUS, SIM_RADIUS));
        for (int i = 0; i < bodies.length; i++) {
            quadTree.Insert(bodies[i]);
        }

        workers = new Thread[threadCount];
        // executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);

        internalBarrier = new CyclicBarrier(threadCount, null);
        barrier = new CyclicBarrier(threadCount + 1, null);
        finished = new AtomicBoolean(false);

        for (int j = 0; j < workers.length; j++) {
            workers[j] = new Thread(new WorkerTask(j));
            workers[j].start();
        }
    }

    @Override
    protected void calculateForces() {
    }

    @Override
    protected void updatePositions() {
        quadTree.ComputePseudoBodies();

        try {
            barrier.await();

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
        int id;

        public WorkerTask(int id) {
            this.start = start;
            this.end = end;
            this.id = id;
        }

        @Override
        public void run() {
            while (!finished.get()) {

                try {
                    barrier.await();
                    calculateForces();

                    internalBarrier.await();

                    updatePositions();

                    barrier.await();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void calculateForces() {

            for (int i = id; i < bodies.length; i += threadCount) {
                bodies[i].force = quadTree.calculateForce(bodies[i]);
            }
        }

        private void updatePositions() {
            Vector2 deltaV;
            Vector2 deltaP;
            for (int i = 0; i < bodies.length; i++) {

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
