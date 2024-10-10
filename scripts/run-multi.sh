#!/bin/bash

# Check if a number is provided as an argument
if [ -z "$1" ]; then
    echo "Please provide the number of clients to start."
    exit 1
fi

# Get the input number from the first argument
number=$1

# Loop from 1 to the input number
for ((i=1; i<=number; i++))
do
    # Add the command you want to run here
    gnome-terminal -- java -jar ./game/target/StrategicSupremacy.jar
done