from ast import If
import xml.etree.ElementTree as ET
from xml.etree import ElementTree
import os
import sys
import xml.etree.ElementTree as xml
import time
from datetime import datetime


class readField:
    folderName = "/"
    fileName = "Field.yolo"

    lines = []

    main = None

    def __init__(self, main):
        self.main = main
        docPath = sys.path[0] + self.folderName + self.fileName
        print("Doc Path: " + docPath)
        file1 = open(docPath, 'r')
        Lines = file1.readlines()
        for line in Lines:
            line = line.strip()
            if(line[0] != ">"):
                #Read lines
                splitLine = line.split("(")
                type = splitLine[0][0]
                shape = splitLine[0][1]
                xy1 = splitLine[1].split(",")
                x1 = float(xy1[0])
                y1 = float(xy1[1].split(")")[0])
                xy2Name = splitLine[2].split(",")
                x2 = float(xy2Name[0])
                y2Name = xy2Name[1].split(")>")
                y2 = float(y2Name[0])
                name = y2Name[1]

                #Fix up lines
                if(type == "R"):
                    x2 = x1+x2
                    y2 = y1+y2

                if(shape == "B"):
                    self.lines.append([x1,y1,x1,y2]) #Left
                    self.lines.append([x1,y2,x2,y2]) #Front
                    self.lines.append([x2,y1,x2,y2]) #Right
                    self.lines.append([x1,y1,x2,y1]) #Back
                else:
                    self.lines.append([x1,y1,x2,y2])
        self.createMirrorField()
                # print("Name>" + name + "< Start>(" + x1 + ", " + y1 + ")< End>(" + x2 + ", " + y2 + ")<")            

    def createMirrorField(self):
        maxY = 0
        maxX = 0
        for i in self.lines:
            if i[0] > maxX: maxX = i[0]
            if i[2] > maxX: maxX = i[2]

            if i[1] > maxY: maxY = i[1]
            if i[3] > maxY: maxY = i[3]
        tempAllLines = self.lines.copy()
        for i in tempAllLines:
            self.lines.append([maxX-i[0], (2*maxY)-i[1], maxX-i[2], (2*maxY)-i[3]])
        print("Finished Mirroring Field")

        # print(self.lines)
    def getField(self):
        return self.lines



# readField()