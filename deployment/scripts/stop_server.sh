#!/bin/bash

# Find the process ID of the running JAR file and terminate it gracefully
PID=$(pgrep -f 'OMG_project-0.0.1-SNAPSHOT.jar')
if [ -z "$PID" ]; then
  echo "No running application found."
else
  echo "Stopping application with PID: $PID"
  kill -15 $PID
  sleep 30
fi