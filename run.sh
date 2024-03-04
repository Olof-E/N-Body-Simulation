#!/bin/bash
javac ./src/App.java -cp ./src/ -d bin/


java -cp ./bin/ App --numBodies $1 --window