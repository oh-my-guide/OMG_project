#!/bin/bash
# Navigate to the directory where the JAR file is located
cd /home/ec2-user/app
echo "init" > start_server_log.txt
# Start the new JAR file
nohup java -jar OMG_project-0.0.1-SNAPSHOT.jar > applog.txt 2> errorlog.txt &
echo "Application started." >> start_server_log.txt