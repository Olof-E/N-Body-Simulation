#!/bin/bash
javac ./src/App.java -cp ./src/ -d bin/


java -cp ./bin/ App -n $1 -s $2 -t $3 -w