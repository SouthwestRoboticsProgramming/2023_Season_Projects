import math
import numpy as np

class Stereo_Helper:
        
    base = 1

    def __init__(self, base):
        self.base = base


    def solveStereo(self, camAngleL, camAngleR):
        
        cam1 = math.tan(math.radians(-camAngleL + 90))
        cam2 = math.tan(math.radians(-camAngleR + 90))

        b = self.base

        if cam1 == cam2:
            return 0.0, 0.0

        x = -((cam2*b)/((cam1-cam2)))
        y = x * cam1

        return(x,y)

    def solveDistance(self, x, y, z):
        d = math.sqrt(math.pow(x,2) + math.pow(y,2) + math.pow(z,2))
        return d

    def solveGlobal(self, d1, d2, centerAngle):

        c = math.sqrt(math.pow(d1,2)+math.pow(d2,2)-2*d1*d2*math.cos(math.radians(centerAngle)))
        x = (d2**2- c**2-d1**2) / (2*c+.00001)
        y = math.sqrt(abs(math.pow(d1,2)-math.pow(x,2)))
    
        return(x,y,c)