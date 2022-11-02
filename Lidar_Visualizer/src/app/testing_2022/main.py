from dataclasses import field
from math import cos, radians, sin
import threading
import os
import time
from enum import Enum

from readField import readField
from visualPlot import visualPlot
from lidarPosition import lidarPosition

class main:

    readField = None
    visualPlot = None
    lidarPosition = None

    # robotPos = [60,60,-160]
    # obotPos = [250,300,-40]
    # robotPos = [160,200,0]

    robotPos = [200,60,40]

    def __init__(self):
        self.readField = readField(self)
        self.visualPlot = visualPlot(self)
        self.lidarPosition = lidarPosition(self)

    def run(self):
        fieldMap = self.readField.getField()
        setRobotCords = self.createRobotCords([25,25], self.robotPos)
        lidarData = []
        simulatedData = self.lidarPosition.simulateData(fieldMap, self.robotPos)
        for i in simulatedData:
            setRobotCords.append([self.robotPos[0], self.robotPos[1], i[2], i[3]])
            lidarData.append([self.robotPos[0], self.robotPos[1], i[0], i[1]])
        foundPos = self.lidarPosition.findRobotLocation(fieldMap, None, lidarData, 5)
        foundRobotCords = self.createRobotCords([25,25], [foundPos[0], foundPos[1], foundPos[2]])
        # foundData = self.lidarPosition.simulateData(fieldMap, foundPos)
        # for i in foundData:
            # foundRobotCords.append([foundPos[0], foundPos[1], i[2], i[3]])
        self.visualPlot.loadPlot(fieldMap, setRobotCords, foundRobotCords)

    def createRobotCords(self, size, robotPos):
        cx1 = robotPos[0]-(size[0]*sin(radians(robotPos[2])))
        cx2 = robotPos[0]+(size[0]*sin(radians(robotPos[2])))
        cy1 = robotPos[1]-(size[1]*cos(radians(robotPos[2])))
        cy2 = robotPos[1]+(size[1]*cos(radians(robotPos[2])))
        s1x1 = cx1-(size[0]*cos(radians(robotPos[2])))
        s1y1 = cy1+(size[0]*sin(radians(robotPos[2])))
        s1x2 = cx1+(size[0]*cos(radians(robotPos[2])))
        s1y2 = cy1-(size[0]*sin(radians(robotPos[2])))
        s2x1 = cx2-(size[0]*cos(radians(robotPos[2])))
        s2y1 = cy2+(size[0]*sin(radians(robotPos[2])))
        s2x2 = cx2+(size[0]*cos(radians(robotPos[2])))
        s2y2 = cy2-(size[0]*sin(radians(robotPos[2])))
        side1 = [s1x1, s1y1, s1x2, s1y2]
        side2 = [s1x1, s1y1, s2x1, s2y1]
        side3 = [s2x1, s2y1, s2x2, s2y2]
        side4 = [s1x2, s1y2, s2x2, s2y2]
        pointer = [robotPos[0], robotPos[1], cx2, cy2]
        return([side1, side2, side3, side4, pointer])


    
        
def init():
    m = main()
    m.run()

if __name__ == "__main__":
    init()