package common;

public abstract class Simulation {
    public final double G_CONSTANT = 6.67545e-11;
    public final double SIM_RADIUS = 1.4959e17;
    public final double DT = 22e-3;

    public Body[] bodies;
    public int simSteps = -1;

    public Simulation(int numBodies, int simSteps) {
        bodies = new Body[numBodies];
        for (int i = 0; i < bodies.length; i++) {
            bodies[i] = new Body(
                    new Vector2((Math.random() * 0.98 + 0.01) * 1280, (Math.random() * 0.98 + 0.01) * 1280),
                    5.97219e24 + (Math.random() * 2.0 - 1.0) * 4.33e24);

        }
    }

    public void Run() {
        if (simSteps == -1) {
            while (true) {
                long startTime = System.nanoTime();
                calculateForces();
                updatePositions();

                if (Window.GetInstance().enabled)
                    Window.GetInstance().updateWindow();

                long endTime = System.nanoTime();

                double runTime = (endTime - startTime) / 1000000.0;
                System.out.printf("Time: %f ms  | FPS: %f\r", runTime, 1000.0 / runTime);
            }
        } else {
            for (int i = 0; i < simSteps; i++) {
                long startTime = System.nanoTime();
                calculateForces();
                updatePositions();
                long endTime = System.nanoTime();

                System.out.printf("Calculations: %f ms\n", 1.0 / ((endTime - startTime) / 1000000000.0));
                if (Window.GetInstance().enabled)
                    Window.GetInstance().updateWindow();
            }
        }
    }

    protected abstract void calculateForces();

    protected abstract void updatePositions();
}
