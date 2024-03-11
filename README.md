# Project ID1217

To run the program be in the same directory as sim-run.sh and execute it with the parameters to use for the simulation. To get a help list of what parameters can be user-defined, use the help flag as in ``./sim-run.sh -h`` or ``./sim-run.sh --help``.

An example would be:

``./sim-run.sh -n 1000 -s 4000 -t 4 -w 4 -win``

The command above would start a simulation of 1000 bodies that would run for 4000 times steps using the parallelized version of Barnes-Hut with 4 workers and the graphical visualization turned on.