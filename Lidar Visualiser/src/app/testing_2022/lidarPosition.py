from ast import Try
from datetime import datetime
from fileinput import close
from math import atan, atan2, radians, sin, sqrt, tan, cos, pi
from random import random
from sqlite3 import Time
from time import time
from datetime import datetime

class lidarPosition:

    main = None

    lidarReadings = 36
    searchGridSize = [12, 4]
    searchAngles = [24, 4]


    def __init__(self, main):
        self.main = main

    def simulateData(self, fieldMap, robotPos):
        """
        \nDef: finds the distence to the nearest wall
        \nInput fieldMap-> ([[x1, y1, x2, y2], ...]) - List of field walls
        \nInput robotPos-> ([x1, y1, angleToWall]) - the info from the lidar
        \nOutputs-> ([[angle, distenceToWall, intersectX, intersectY], ...]) - of intercept of wall
        """
        def getRandReadingsAngles():
            readings = []
            totalReadings = self.lidarReadings
            for i in range(totalReadings):
                angle = 360.0*((i+1)/totalReadings)
                variation = 360*(1/totalReadings)*random()
                readings.append(angle-variation)
            return readings
        
        readings = getRandReadingsAngles()
        simulatedData = []
        for j in readings:
            hitwall = self.findDistenceToClosestWall(fieldMap, [robotPos[0], robotPos[1], j])
            if hitwall != None:
                hitwall[0] -= robotPos[2]
                simulatedData.append(hitwall)
        return(simulatedData)

    def findRobotLocation(self, fieldMap, searchBox, robotData, tolerence):
        """
        \nDef: recieves lidar data and field map and finds robots location on field
        \nInput fieldMap-> ([[x1, y1, x2, y2], ...]) - List of field walls
        \nInput searchBox-> 
        \n    1) (None) - Signals the search of the whole field
        \n    2) ([x1, y1, x2, y2, a1, a2]) - box to search for robots position
        \nInput robotData-> ([[x1, y1, angle, distenctToWall], ...]) - the info from the lidar
        \nInput tolerence-> (float) - how percicely to search for robot's position (inches)
        \nOutputs-> ([x, y, angle, sumOfLidarInaccercy's]) - of intercept of wall
        """
        
        def getTime():
            # print("Current date:",datetime.utcnow())
            date= datetime.utcnow() - datetime(1970, 1, 1)
            # print("Number of days since epoch:",date)
            seconds =(date.total_seconds())
            milliseconds = round(seconds*1000)
            # print("Milliseconds since epoch:",milliseconds)
            return milliseconds
        startTime = getTime()

        searchgrid = self.searchGridSize
        turnAngle = self.searchAngles
        if(searchBox == None):
            maxX = 0
            maxY = 0
            for i in fieldMap:
                if (i[0] > maxX): maxX = i[0]
                if (i[2] > maxX): maxX = i[2]
                if (i[1] > maxY): maxY = i[1]
                if (i[3] > maxY): maxY = i[3]
            searchBox = [0,0,maxX,(maxY/2), -180, 180]

        currentTolerence = None

        indexer = 0
        while (currentTolerence == None or currentTolerence > tolerence):
            indexer +=1
            grid = []
            if (indexer == 1):
                thisSearchgrid = searchgrid[0]
                thisTurnAngle = turnAngle[0]
            else:
                thisSearchgrid = searchgrid[1]
                thisTurnAngle = turnAngle[1]

            for i in range(thisSearchgrid):
                for j in range(thisSearchgrid):
                    pointX = (((searchBox[2]-searchBox[0])/(thisSearchgrid+1))*(i+1))+searchBox[0]
                    pointY = (((searchBox[3]-searchBox[1])/(thisSearchgrid+1))*(j+1))+searchBox[1]
                    grid.append([pointX, pointY])

            #Find robot location
            pointData = []
            for i in range(len(grid)): #GRID: Find robots position for each grid point
                anglesData = [] #[[sum of distence offsets], ...]
                for j in range(thisTurnAngle): #ANGLES: At each point, find location for multiple angles
                    #find test angles
                    # angle = (360*(j/turnAngles))-180
                    angle = ((searchBox[5]-searchBox[4])*(j/thisTurnAngle))+searchBox[4]
                    angleData = [] #[[angle, distence], ...]
                    for k in robotData: #LIDAR: Test each robot's exact Angle for intersection of wall to match with lists later
                        searchAngle = self.clampAngle(k[2]+angle)
                        distenceToClosestWall = self.findDistenceToClosestWall(fieldMap, [grid[i][0], grid[i][1], searchAngle])
                        # distenceToClosestWall = self.findDistenceToClosestWall(fieldMap, [k[0], k[1], searchAngle])
                        if (distenceToClosestWall != None):
                            angleData.append([angle, searchAngle, distenceToClosestWall[1]])
                        else:
                            angleData.append([angle, searchAngle, None])
                    sumOfDistenceOffsets = 0
                    for k in range(len(robotData)):
                        recDis = robotData[k][3]
                        calcDis = angleData[k][2]
                        if (recDis != None and calcDis != None):
                            sumOfDistenceOffsets += abs(recDis-calcDis)
                    anglesData.append([grid[i][0], grid[i][1], angleData[0][0], sumOfDistenceOffsets])
                smallestDistence = None
                for j in anglesData:
                    if (smallestDistence == None):
                        smallestDistence = j
                    elif (j[3] < smallestDistence[3]):
                        smallestDistence = j
                pointData.append(smallestDistence)
                # print("Working on " + str(i))
            closestPoint = None
            for i in pointData:
                if (closestPoint == None):
                    closestPoint = i
                elif (i[3] < closestPoint[3]):
                    closestPoint = i
            print("Execution Time = " + str(getTime()-startTime) + "ms")
            print(closestPoint)
            
            xTolerence = (searchBox[2]-searchBox[0])/thisSearchgrid
            yTolerence = (searchBox[3]-searchBox[1])/thisSearchgrid
            currentTolerence = sqrt(pow(xTolerence,2) + pow(yTolerence,2))
            newSearchX = [closestPoint[0]-xTolerence, closestPoint[0]+xTolerence]
            newSearchY = [closestPoint[1]-yTolerence, closestPoint[1]+yTolerence]
            newSearchA = (searchBox[5]-searchBox[4])/thisTurnAngle
            searchBox = [newSearchX[0], newSearchY[0], newSearchX[1], newSearchY[1], closestPoint[2]-newSearchA, closestPoint[2]+newSearchA]
        return closestPoint

        # print(pointData)


    def findDistenceToClosestWall(self, fieldMap, robotPos):
        """
        \nDef: finds the distence to the nearest wall
        \nInput fieldMap-> ([[x1, y1, x2, y2], ...]) - List of field walls
        \nInput robotPos-> ([x1, y1, angleToWall]) - the info from the lidar
        \nOutputs-> ([angle, distenceToWall, intersectX, intersectY]) - of intercept of wall
        """
        closestDistence = None
        for i in fieldMap:
            robotVec = [robotPos[0], robotPos[1], robotPos[2]]
            wallVex = [[i[0],i[1]], [i[2],i[3]]]
            intersec = self.findLineIntersection(wallVex, robotVec)
            if(intersec != False):
                distenceToWall = sqrt(pow(intersec[0]-robotPos[0], 2)+pow(intersec[1]-robotPos[1], 2))
                hitwallVal = [robotPos[2], distenceToWall, intersec[0], intersec[1]]
                if(closestDistence == None):
                    closestDistence=hitwallVal
                elif(distenceToWall < closestDistence[1]):
                    closestDistence=hitwallVal
        return closestDistence

    def clampAngle(self, angle):
        """
        \nDef: Clamps an angle to the range of -180 to 180
        \nInput angle-> (float) Angle in degrees
        \nOutputs-> (float) Clamped angle in degrees
        """
        while (angle > 180):
            angle = angle-360
        while (angle < -180):
            angle = angle+360
        return angle
        
        
    def findLineIntersection(self, wall, robot):
        """
        \nDef: Finds the point where the vector from the robot hits a wall
        \nInput wall-> ([[x1, y1], [x2, y2]])
        \nInput robot-> ([x1, y1, angle])
        \nOutputs->
        \n    1) ([x, y]) - of intercept of wall
        \n    2) (False) - When robot does not intercept the wall
        """
        robot = [[robot[0], robot[1]], [robot[0]+sin(radians(robot[2])), robot[1]+cos(radians(robot[2]))]]

        xdiff = (wall[0][0] - wall[1][0], robot[0][0] - robot[1][0])
        ydiff = (wall[0][1] - wall[1][1], robot[0][1] - robot[1][1])

        def det(a, b):
            return a[0] * b[1] - a[1] * b[0]

        div = det(xdiff, ydiff)
        if div == 0:
            return(False)

        d = (det(*wall), det(*robot))
        x = det(d, xdiff) / div
        y = det(d, ydiff) / div
        if(x > max(wall[0][0],wall[1][0])): return(False)
        if(x < min(wall[0][0],wall[1][0])): return(False)
        if(y > max(wall[0][1],wall[1][1])): return(False)
        if(y < min(wall[0][1],wall[1][1])): return(False)
        
        if(-xdiff[1] > 0.0 and x<robot[0][0]): return(False)
        if(-xdiff[1] < 0.0 and x>robot[0][0]): return(False)
        if(-ydiff[1] > 0.0 and y<robot[0][1]): return(False)
        if(-ydiff[1] < 0.0 and y>robot[0][1]): return(False)
            

        return([x,y])


# data = readField(None).simulateData([[0,0,0,100],
#                                     [0,100,100,100],
#                                     [100,100,100,0],
#                                     [100,0,0,0]
#                                 ], [50,50,160])
# for i in data:
#     print(i)
