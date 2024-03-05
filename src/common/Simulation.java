package common;

import java.nio.charset.Charset;

public abstract class Simulation {
    public static final double G_CONSTANT = 6.67545e-11;
    public static final double SIM_RADIUS = 1.4959e17;
    public static final double DT = 2e-10;

    public Body[] bodies;
    public int simSteps = -1;

    public boolean terminalCompatibility = false;

    public Simulation(int numBodies, int simSteps) {
        bodies = new Body[numBodies];
        this.simSteps = simSteps;
        for (int i = 0; i < bodies.length; i++) {
            bodies[i] = new Body(
                    new Vector2((Math.random() * 0.98 + 0.01) * 1280, (Math.random() * 0.98 + 0.01) * 1280),
                    5.97219e24 + (Math.random() * 2.0 - 1.0) * 4.33e24);

        }
    }

    public void Run() {
        System.out.println(Charset.defaultCharset());
        if (simSteps == -1) {
            while (true) {
                long startTime = System.nanoTime();
                calculateForces();
                updatePositions();

                if (Window.GetInstance().enabled)
                    Window.GetInstance().updateWindow();

                long endTime = System.nanoTime();

                double runTime = (endTime - startTime) / 1000000.0;

                System.out.printf("Compute Time: %f ms | steps/s: %f \r", runTime, 1000.0 / runTime);
            }
        } else {
            for (int i = 0; i < simSteps; i++) {
                long startTime = System.nanoTime();
                calculateForces();
                updatePositions();
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

    protected abstract void calculateForces();

    protected abstract void updatePositions();
}
