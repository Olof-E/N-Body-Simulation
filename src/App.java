import common.*;

public class App {
    public enum SimType {
        SEQUENTIAL_STD,
        SEQUENTIAL_PAR,
        BARNES_STD,
        BARNES_PAR
    }

    // Default values of program parameters
    private static int numBodies = 150;
    private static int simSteps = -1;
    private static int threadCount = 2;
    private static boolean visualizationEnabled = false;
    private static boolean terminalCompatibility = false;
    private static boolean debugMode = false;

    private static SimType simEngineType = SimType.SEQUENTIAL_PAR;

    public static void main(String[] args) throws Exception {

        // Retrieve and process arguments supplied by user, if any
        int argI = 0;
        while (argI < args.length && (args[argI].startsWith("-") || args[argI].startsWith("--"))) {
            if (args[argI].equals("-h") || args[argI].equals("--help")) {
                cmdHelp();
                return;
            } else if (args[argI].equals("-n") || args[argI].equals("--num-bodies")) {
                numBodies = Integer.parseInt(args[++argI]);
            } else if (args[argI].equals("-s") || args[argI].equals("--sim-steps")) {
                simSteps = Integer.parseInt(args[++argI]);
            } else if (args[argI].equals("-win") || args[argI].equals("--window")) {
                visualizationEnabled = true;
            } else if (args[argI].equals("-tc") || args[argI].equals("--terminal-compatibility")) {
                terminalCompatibility = true;
            } else if (args[argI].equals("-t") || args[argI].equals("--engine-type")) {
                simEngineType = SimType.values()[Integer.parseInt(args[++argI]) - 1];
            } else if (args[argI].equals("-w") || args[argI].equals("--thread-count")) {
                threadCount = Integer.parseInt(args[++argI]);
            } else if (args[argI].equals("-d") || args[argI].equals("--debug")) {
                debugMode = true;
            } else {
                System.out.printf("%s not recognized as an argument\n", args[argI]);
                cmdHelp();
            }
            argI++;
        }

        // Initialize the simulation
        Simulation simulation;
        if (simEngineType == SimType.SEQUENTIAL_STD)
            simulation = new normal.SeqSimulation(numBodies, simSteps);
        else if (simEngineType == SimType.SEQUENTIAL_PAR)
            simulation = new normal.ParSimulation(numBodies, simSteps, threadCount);
        else if (simEngineType == SimType.BARNES_STD)
            simulation = new barnesHut.SeqSimulation(numBodies, simSteps);
        else
            simulation = new barnesHut.ParSimulation(numBodies, simSteps, threadCount);

        simulation.terminalCompatibility = terminalCompatibility;

        // Link window instance to simulation data if enabled
        if (visualizationEnabled) {
            Window.GetInstance().Init(new Vector2(1280, 1280), debugMode);
            Window.GetInstance().enabled = true;
            Window.GetInstance().LinkData(simulation.bodies, simulation.quadTree);
        }

        // Start the simulation
        simulation.Run();

        // Close window
        if (visualizationEnabled)
            Window.GetInstance().Close();

        // Tell program to completely exit
        System.exit(0);

    }

    // Print help msg to console
    private static void cmdHelp() {
        System.err.println("Usage: ./sim-run.sh [options]");
        System.err.println("Options are:");
        System.err.println("    -n or --num-bodies <amount> |  set number of bodies");
        System.err.println("    -s or --sim-steps <steps> | Excluded or set to -1, for endless simulation");
        System.err.println("    -win or --window | Enable the visualization");
        System.err.println("    -tc or --terminal-compatibility | Switch progress bar render mode");
        System.err.println(
                "    -t or --engine-type <index> ");
        System.out.println(
                "\t* 1 - Sequential brute-force\n\t* 2 - Parallel brute-force\n\t* 3 - Sequential Barnes-Hut\n\t* 4 - Parallel Barnes-Hut");
        System.err.println("    -w or --thread-count <amount> | How many threads to create");

        System.err.println("    -d or --debug-mode | Switch on debug mode");

        System.exit(1);
    }
}