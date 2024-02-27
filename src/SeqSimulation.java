public class SeqSimulation {
    public static final double G_CONSTANT = 6.67545e-11;
    public static final double SIM_RADIUS = 1.4959e17;
    public static final double DT = 11e-3;
    static Body[] bodies;

    public static void main(String[] args) throws Exception {
        bodies = new Body[1500];
        for (int i = 0; i < bodies.length; i++) {
            bodies[i] = new Body(
                    new Vector2(Math.random() * 1280, Math.random() * 720),
                    5.97219e24 + (Math.random() * 2.0 - 1.0) * 4.33e24);

        }

        Window window = new Window();
        window.CreateWindow(bodies);

        while (true) {
            long startTime = System.nanoTime();
            calculateForces();
            updatePositions();
            long endTime = System.nanoTime();

            System.out.printf("Calculations: %d ms\n", (endTime - startTime) / 1000000);
            window.updateCanvas();
        }
    }

    public static Vector2 scaleUp(Vector2 vec) {
        return new Vector2((vec.x / 1280) * SIM_RADIUS, (vec.y / 720) * SIM_RADIUS);
    }

    public static void calculateForces() {
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

    public static void updatePositions() {
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
                bodies[i].velocity.x = -bodies[i].velocity.x / 2;
            } else if (bodies[i].position.x >= 1280) {
                bodies[i].position.x = 1279;
                bodies[i].velocity.x = -bodies[i].velocity.x / 2;
            }

            if (bodies[i].position.y <= 0) {
                bodies[i].position.y = 1;
                bodies[i].velocity.y = -bodies[i].velocity.y / 2;
            } else if (bodies[i].position.y >= 720) {
                bodies[i].position.y = 719;
                bodies[i].velocity.y = -bodies[i].velocity.y / 2;
            }
        }
    }
}
