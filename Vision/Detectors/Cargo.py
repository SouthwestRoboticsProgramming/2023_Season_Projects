import numpy as np
import cv2
import math

class Cargo_Detector:

    fov_horizontal = 50
    fov_vertical = 20

    h_min = 0
    h_max = 255
    s_min = 0
    s_max = 255
    v_min = 0
    v_max = 255
    t_low = 0
    t_high = 255

    useCircles = False

    lastShape = [0,0]
    lastFov = [0,0]

    pixelDistance_horizontal = 0
    pixelDistance_vertical = 0

    angle_horizontal = 0
    angle_horizontal2 = 0
    angle_vertical = 0

    maskedFrame = None

    def __init__(self, fov):
        self.fov_horizontal = fov[0]
        self.fov_vertical = fov[1]
        return



    def periodic(self, rawFrame):

        h_min = self.h_min
        h_max = self.h_max
        s_min = self.s_min
        s_max = self.s_max
        v_min = self.v_min
        v_max = self.v_max

        # Set all of the angles to 0 so that if something fails, it is detectable
        self.angle_horizontal = 0
        self.angle_horizontal2 = 0
        self.angle_vertical = 0


        # Check that the camera properties haven't changed
        currentFov = [self.fov_horizontal, self.fov_vertical]
        if rawFrame.shape != self.lastShape or currentFov != self.lastFov:
            self.pixelDistance_horizontal = (0.5 * rawFrame.shape[1]) / (math.tan(math.radians(0.5 * self.fov_horizontal)))
            self.pixelDistance_vertical = (0.5 * rawFrame.shape[0]) / (math.tan(math.radians(0.5 * self.fov_vertical)))
            pass

        # Mask off the object using HSV

        lower = np.array([h_min, s_min, v_min])
        upper = np.array([h_max, s_max, v_max])

        lower2 = None
        upper2 = None

        # If the hue is inverted min > max, then invert the spectrum so that it can detect red
        if h_max < h_min:
            lower = np.array([h_min, s_min, v_min])
            upper = np.array([180, s_max, v_max])

            lower = np.array([0, s_min, v_min])
            upper = np.array([h_max, s_max, v_max])
            pass

        # Blur the image to remove noise
        blur = cv2.GaussianBlur(rawFrame,(5,5),0)
        hsv = cv2.cvtColor(blur, cv2.COLOR_BGR2HSV)
        gray = cv2.cvtColor(blur,cv2.COLOR_BGR2GRAY)
        mask = cv2.inRange(hsv,lower,upper)
        maskedColor = cv2.bitwise_and(hsv, hsv, mask=mask)

        if lower2 is not None and upper2 is not None:
            mask = cv2.inRange(maskedColor, lower2, upper2)
        
        grayMask = cv2.bitwise_and(gray,gray,mask=mask)

        self.maskedFrame = cv2.bitwise_and(rawFrame, rawFrame, mask=mask)

        # Create binary mask for contour filtering
        ret, binary = cv2.threshold(grayMask, self.t_low, self.t_high, cv2.THRESH_BINARY)

        # Find contours in the mask
        contours, hierarchy = cv2.findContours(binary,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
        edges = cv2.Canny(binary,50,50)

        if self.useCircles:
            params = cv2.SimpleBlobDetector_Params()

            params.filterByArea = False
            params.minArea = 1000
            
            params.filterByCircularity = True
            params.minCircularity = 0.1

            params.filterByConvexity = False
            params.minConvexity = 0.1

            params.filterByInertia = False
            params.minInertiaRatio = 0.001

            detector = cv2.SimpleBlobDetector_create(params)
            keypoints = detector.detect(edges)

            self.maskedFrame = cv2.drawKeypoints(self.maskedFrame, keypoints, 0, (255,0,0), cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)

            contours2, hierarchy = cv2.findContours(binary, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

            # If the circle detection fails, use normal filtering instead
            if len(contours2) > 0:
                contours = contours2

        # Create a bounding box around the ball
        if len(contours) > 0:
            bestContour = max(contours, key= cv2.contourArea)

            x, y, w, h = cv2.boundingRect(bestContour)

            # Calculate angles to target
            self.angle_horizontal = math.degrees(math.atan2((x + 0.5* w) - (rawFrame.shape[1]/2), self.pixelDistance_horizontal))
            self.angle_vertical = math.degrees(math.atan2(((y+ 0.5 * h) - rawFrame.shape[0]/2), self.pixelDistance_vertical))
            self.angle_horizontal2 = math.degrees(math.atan2(x - rawFrame.shape[1]/2, self.pixelDistance_horizontal))

            cv2.rectangle(self.maskedFrame, (x,y),( x + w,y + h ), (255,0,255),3)

        self.lastShape = rawFrame.shape
        self.lastFov = [self.fov_horizontal, self.fov_vertical]
        return

    def useCircleDetection(self,useCircleDetection):
        self.useCircles = useCircleDetection
        return

    def setFOV(self, hor_fov, vert_fov):
        self.fov_horizontal = hor_fov
        self.fov_vertical - vert_fov
        return
    
    def setHue(self, min, max):
        self.h_min = min
        self.h_max = max
        return

    def setSat(self, min, max):
        self.s_min = min
        self.s_max = max
        return

    def setValue(self, min, max):
        self.s_min = min
        self.s_max = max
        return

    def getMaskedFrame(self):
        return self.maskedFrame
    
    def getMask(self):
        return self.mask
    
    def getAngles(self):
        return (self.angle_horizontal, self.angle_horizontal2, self.angle_vertical)