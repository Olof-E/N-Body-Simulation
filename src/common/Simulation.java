package common;

import barnesHut.QuadTree;

public abstract class Simulation {
    public static final double G_CONSTANT = 6.67545e-11;
    public static final double SIM_RADIUS = 1.4959e17;
    public static final double DT = 1e-5;

    public Body[] bodies;
    public QuadTree quadTree;
    public int simSteps = -1;

    public boolean terminalCompatibility = false;

    public Simulation(int numBodies, int simSteps) {
        bodies = new Body[numBodies];
        this.simSteps = simSteps;
        for (int i = 0; i < bodies.length; i++) {
            bodies[i] = new Body(
                    new Vector2((Math.random() * 0.98 + 0.01) * SIM_RADIUS, (Math.random() * 0.98 + 0.01) * SIM_RADIUS),
                    5.97219e24 + (Math.random() * 2.0 - 1.0) * 4.33e24);

        }
    }

    public void Run() {
        if (simSteps == -1) {
            while (true) {
                long startTime = System.nanoTime();
                calculateForces();
                updatePositions();

                long endTime = System.nanoTime();

                double runTime = (endTime - startTime) / 1000000.0;

                System.out.printf("Compute Time: %f ms | steps/s: %.1f \r", runTime, 1000.0 / runTime);

                if (Window.GetInstance().enabled)
                    Window.GetInstance().updateWindow();
            }
        } else {
            double[] times = new double[simSteps];
            for (int i = 0; i < simSteps; i++) {
                long startTime = System.nanoTime();
                calculateForces();
                updatePositions();
                long endTime = System.nanoTime();

                double runTime = (endTime - startTime) / 1000000.0;

                char remaining = terminalCompatibility ? ' ' : '░';
                char finished = terminalCompatibility ? '#' : '█';

                String progBar = " [";
                double progress = i / (double) simSteps;
                for (int j = 0; j < 40; j++) {
                    if (j / 40.0 < progress)
                        progBar += finished;
                    else
                        progBar += remaining;
                }
                progBar += "]";
                times[i] = runTime;
                System.out.printf("%s  %d / %d | steps/s: %.1f \r", progBar, i + 1, simSteps, 1000.0 / runTime);
                if (Window.GetInstance().enabled)
                    Window.GetInstance().updateWindow();
            }

            double totalTime = 0;
            for (int i = 0; i < times.length; i++) {
                totalTime += times[i];
            }
            System.out.println("\n|==============================================================================|");
            System.out.printf(" Avg. time/step: %.3f ms | ", totalTime / simSteps);
            System.out.printf("Total execution time: %.3f s\n", totalTime / 1000.0);
        }
    }

    protected abstract void calculateForces();

    protected abstract void updatePositions();
}
