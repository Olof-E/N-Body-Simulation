# Project ID1217
This project is an implementation of a solver for the N-Body physics problem. This particular implementation features two different algorithms for simulating N bodies, each of which has both a sequential and parallel version.


## Compile and Run
To run the program, make sure to be in the same directory as the sim-run.sh script and execute it with the parameters to use for the simulation. To get a help list of what parameters can be user-defined, use the help flag as in ``./sim-run.sh -h`` or ``./sim-run.sh --help``.

An example of how to run the program would be:

``./sim-run.sh -n 1000 -s 4000 -t 4 -w 4 -win``

The example command above would start a simulation of 1000 bodies that would run for 4000 times steps using the parallelized version of Barnes-Hut with 4 workers and the graphical visualization turned on.

## Arguments
### **-h** or **--help** |> This flag can be set to tell the program to print out help information to the console

<hr>

### **-n \<amount>** or **--num-bodies \<amount>** |>Can be used to set the size of the simulation

<hr>

### **-s \<steps>** or **--sim-steps \<steps>** |> Can be used to tell the program how many units of time to compute

<hr>

### **-win** or **--window** |> Can be used to tell the program to create window to display a simulation visualization

<hr>

### **-tc** or **--terminal-compatibility** |> Can be used to tell the program to use a more general output form to the console. (Can help if progress bar is rendered weirdly)

<hr>

### **-t \<type>** or **--engine-type \<type>** |> Can be used to tell the program what simulation algorithm to use
* **(type) - Algorithm Name**
* (1) - Sequential Brute-force (Default) 
* (2) - Parallel Brute-force
* (3) - Sequential Barnes-Hut
* (4) - Parallel Barnes-Hut

<hr>

### **-w \<amount>** or **--thread-count \<amount>** |> Can be used to tell the program how many worker threads to create for parallelized computation

<hr>

### **-d** or **--debug-mode** |> This flag can be set to tell the program to render additional information to the visualization window

