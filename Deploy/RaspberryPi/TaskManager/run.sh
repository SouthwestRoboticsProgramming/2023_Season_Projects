#!/bin/bash

rm log.log
/home/pi/jre/bin/java -jar TaskManager-2023.jar 2>&1 | tee log.log
