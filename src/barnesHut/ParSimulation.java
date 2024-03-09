package barnesHut;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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

    public QuadTree quadTree;
    private ThreadPoolExecutor executor;

    public CyclicBarrier barrier;

    private int threadCount = 40;

    public ParSimulation(int numBodies, int simSteps) {
        super(numBodies, simSteps);

        quadTree = new QuadTree(new Vector2(1280, 1280));
        for (int i = 0; i < bodies.length; i++) {
            quadTree.Insert(bodies[i]);
        }

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(16);
        barrier = new CyclicBarrier(16, null);
    }

    @Override
    public void Run() {
        if (simSteps == -1) {
            while (true) {
                long startTime = System.nanoTime();
                RunTasks();
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                if (Window.GetInstance().enabled)
                    Window.GetInstance().updateWindow();

                long endTime = System.nanoTime();

                double runTime = (endTime - startTime) / 1000000.0;

                System.out.printf("Compute Time: %f ms | steps/s: %f \r", runTime, 1000.0 / runTime);
            }
        } else {
            for (int i = 0; i < simSteps; i++) {
                long startTime = System.nanoTime();
                RunTasks();
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                long endTime = System.nanoTime();

                double runTime = (endTime - startTime) / 1000000.0;

                char remaining = terminalCompatibility ? ' ' : '░';
                char finished = terminalCompatibility ? '#' : '█';

                String progBar = "[";
                double progress = i / (double) simSteps;
                for (int j = 0; j < 40; j++) {
                    if (j / 40.0 < progress)
                        progBar += finished;
                    else
                        progBar += remaining;
                }
                progBar += "]";
                System.out.printf("%s  %d / %d | steps/s: %f \r", progBar, i + 1, simSteps, 1000.0 / runTime);
                if (Window.GetInstance().enabled)
                    Window.GetInstance().updateWindow();
            }
        }
    }

    private void RunTasks() {
        for (int i = 0; i < threadCount; i++) {
            int start = i * bodies.length / threadCount;
            int end = start + bodies.length / threadCount;
            if (i == threadCount - 1) {
                end = bodies.length;
            }
            executor.execute(new WorkerTask(start, end));
        }
    }

    @Override
    protected void calculateForces() {
    }

    @Override
    protected void updatePositions() {
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
                barrier.await();

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
            quadTree.ComputePseudoBodies();
            for (int i = start; i < end; i++) {
                bodies[i].force = quadTree.calculateForce(bodies[i]);
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
            quadTree = new QuadTree(new Vector2(1280, 1280));
            for (int i = 0; i < bodies.length; i++) {
                quadTree.Insert(bodies[i]);
            }
            Window.GetInstance().LinkData(bodies, quadTree);
        }
    }
}
