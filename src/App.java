import common.*;

public class App {
    public enum SimType {
        SEQUENTIAL_STD,
        SEQUENTIAL_PAR,
        BARNES_STD,
        BARNES_PAR
    }

    private static int numBodies = 150;
    private static int simSteps = -1;
    private static boolean visualizationEnabled = false;
    private static boolean terminalCompatibility = false;

    private static SimType simEngineType = SimType.SEQUENTIAL_PAR;

    public static void main(String[] args) throws Exception {
        int argI = 0;

        while (argI < args.length && (args[argI].startsWith("-") || args[argI].startsWith("--"))) {
            if (args[argI].equals("-n") || args[argI].equals("--num-bodies")) {
                numBodies = Integer.parseInt(args[++argI]);
            } else if (args[argI].equals("-s") || args[argI].equals("--sim-steps")) {
                simSteps = Integer.parseInt(args[++argI]);

            } else if (args[argI].equals("-w") || args[argI].equals("--window")) {
                visualizationEnabled = true;
            } else if (args[argI].equals("-tc") || args[argI].equals("--terminal-compatibility")) {
                terminalCompatibility = true;
            } else if (args[argI].equals("-t") || args[argI].equals("--engine-type")) {
                simEngineType = SimType.values()[Integer.parseInt(args[++argI]) - 1];
            } else {
                cmdHelp();
            }
            argI++;
        }

        try {
            Simulation simulation;
            if (simEngineType == SimType.SEQUENTIAL_STD)
                simulation = new normal.SeqSimulation(numBodies, simSteps);
            else if (simEngineType == SimType.SEQUENTIAL_PAR)
                simulation = new normal.ParSimulation(numBodies, simSteps);
            else if (simEngineType == SimType.BARNES_STD)
                simulation = new barnesHut.SeqSimulation(numBodies, simSteps);
            else
                simulation = new barnesHut.ParSimulation(numBodies, simSteps);

            simulation.terminalCompatibility = terminalCompatibility;

            if (visualizationEnabled) {
                Window.GetInstance().Init(new Vector2(1280, 1280));
                Window.GetInstance().enabled = true;
                Window.GetInstance().LinkData(simulation.bodies, simulation.quadTree);
            }

            simulation.Run();

            if (visualizationEnabled)
                Window.GetInstance().Close();

            System.exit(0);
        } catch (Exception e) {
            System.out.println("\nSimulation failed");
            System.err.println("Error shown below: ");
            e.printStackTrace();
        }
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