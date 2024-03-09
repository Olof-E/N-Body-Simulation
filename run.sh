#!/bin/bash
javac ./src/App.java -cp ./src/ -d bin/


java -cp ./bin/ App -n $1 -w -s $2 -t $3