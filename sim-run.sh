#!/bin/bash
javac ./src/App.java -cp ./src/ -d bin/


java -cp ./bin/ App "$@"