import common.*;

public class App {

    private static int numBodies = 150;
    private static int simSteps = -1;
    private static boolean visualizationEnabled = false;
    private static boolean terminalCompatibility = false;

    public static void main(String[] args) throws Exception {
        int argI = 0;

        while (argI < args.length && (args[argI].startsWith("-") || args[argI].startsWith("--"))) {
            if (args[argI].equals("-n") || args[argI].equals("--numBodies")) {
                numBodies = Integer.parseInt(args[++argI]);
            } else if (args[argI].equals("-s") || args[argI].equals("--simSteps")) {
                simSteps = Integer.parseInt(args[++argI]);

            } else if (args[argI].equals("-w") || args[argI].equals("--window")) {
                visualizationEnabled = true;
            } else if (args[argI].equals("-tc") || args[argI].equals("--terminal-compatibility")) {
                terminalCompatibility = true;
            } else {
                cmdHelp();
            }
            argI++;
        }

        try {
            Simulation simulation = new normal.SeqSimulation(numBodies, simSteps);
            simulation.terminalCompatibility = terminalCompatibility;

            if (visualizationEnabled) {
                Window.GetInstance().enabled = true;
                if (simulation.getClass().getPackageName() == "barnesHut")
                    Window.GetInstance().LinkData(simulation.bodies, ((barnesHut.SeqSimulation) simulation).quadTree);

                Window.GetInstance().LinkData(simulation.bodies);
            }

            simulation.Run();

            if (visualizationEnabled)
                Window.GetInstance().Close();

            System.out.println("\nSimulation finished");
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