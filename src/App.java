import common.Body;
import common.Simulation;
import common.Vector2;
import common.Window;
import normal.SeqSimulation;

public class App {

    private static int numBodies = 150;
    private static int simSteps = -1;
    private static boolean visualizationEnabled = false;

    public static void main(String[] args) throws Exception {
        int argI = 0;

        while (argI < args.length && args[argI].startsWith("--")) {
            if (args[argI].equals("--numBodies")) {
                numBodies = Integer.parseInt(args[++argI]);
            } else if (args[argI].equals("--simSteps")) {
                simSteps = Integer.parseInt(args[++argI]);

            } else if (args[argI].equals("--window")) {
                visualizationEnabled = true;
                Window.GetInstance().enabled = true;
            } else {
                cmdHelp();
            }
            argI++;
        }

        Simulation simulation = new SeqSimulation(numBodies, simSteps);

        if (visualizationEnabled)
            Window.GetInstance().LinkData(simulation.bodies);

        simulation.Run();

        if (visualizationEnabled)
            Window.GetInstance().Close();
    }

    private static void cmdHelp() {
        System.err.println("Usage: Simulation [options]");
        System.err.println("Options are:");
        System.err.println("    --numBodies <amount> |  set number of bodies");
        System.err.println("    --simSteps <steps> | Excluded or set to -1, for endless simulation");
        System.err.println("    --window | Enable the visualization");
        System.exit(1);
    }
}