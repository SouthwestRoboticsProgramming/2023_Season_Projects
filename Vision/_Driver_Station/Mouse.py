import cv2
import numpy as np

class Pixel_Getter:

    mouseX = 0
    mouseY = 0

    includedPixels = np.array([[],[],[]])
    excludedPixels = np.array([[],[],[]])

    frame = None

    squareSize = 40

    def _updateMouse(self, event, x, y, flags, param):

        # Only update the mouse position if it is moving
        if event == cv2.EVENT_MOUSEMOVE:
            self.mouseX = round(x)
            self.mouseY = round(y)

        if event == cv2.EVENT_LBUTTONDOWN:
            # Get the pixel cluster and add it to the list of included pixels
            hues, saturations, values = self.getPixels()
            self.include(hues, saturations, values)
            print("L-Button Clicked")
            pass

        # TODO: Find a better binding for remove: The current freezes the program and the same button is used to zoom in
        if event == cv2.EVENT_RBUTTONDOWN:
            # Get the pixel cluster and remove any that are in the included pixels. Add the pixels to a list to ignore
            print("R-Button Clicked")
            pass
        pass

    def __init__(self, windowName):
        cv2.setMouseCallback(windowName, self._updateMouse)


    def getPixels(self):
        width = self.squareSize
        frame = self.frame

        x = int(self.mouseX - 0.5 * width)
        y = int(self.mouseY - 0.5 * width)

        x2 = int(self.mouseX + 0.5 * width)
        y2 = int(self.mouseY + 0.5 * width)

        pixelCount = width * width
        hues = [None] * pixelCount
        saturations = [None] * pixelCount
        values = [None] * pixelCount
        for i in range(x, x2):
            for j in range(y, y2):
                index = (i - x) + (j - y) * width
                hues[index] = frame[y, x, 0]
                saturations[index] = frame[y, x, 1]
                values[index] = frame[y, x, 2]
        
        return hues, saturations, values


    def setFrame(self, frame):
        self.frame = frame

    def getFrame(self):
        mouseX, mouseY = self.mouseX, self.mouseY
        size = 0.5 * self.squareSize
        pt1 = (int(mouseX - size), int(mouseY - size))
        pt2 = (int(mouseX + size), int(mouseY + size))
        # if size < mouseX < (self.frame.shape[1]-size) and size < mouseY < (self.frame.shape[0]-size):
        newFrame = cv2.rectangle(self.frame, pt1, pt2, (255,255,0))
        # else:
        #     newFrame = self.frame
        # newFrame = cv2.rectangle(self.frame, (0,0), (50,50), (255,255,0))
        # newFrame = self.frame
        return newFrame

    def include(self, hues, sats, values):
        hues = np.concatenate((self.includedPixels[0], hues))
        sats = np.concatenate((self.includedPixels[1], sats))
        values = np.concatenate((self.includedPixels[2], values))
        print(hues)
        # self.includedPixels[0] = np.sort(hues)
        # self.includedPixels[1] = np.sort(sats)
        # self.includedPixels[2] = np.sort(values)

        # self.includedPixels[0] = hues
        # self.includedPixels[1] = sats
        # self.includedPixels[2] = values

        print(self.includedPixels[0])
        pass

    def exclude(self, hues, sats, values):
        hues = np.concatenate((self.excludedPixels[0], hues))
        sats = np.concatenate((self.excludedPixels[1], sats))
        values = np.concatenate((self.excludedPixels[2], sats))
        self.excludedPixels[0] = np.sort()
        self.excludedPixels[1] = np.sort()
        self.excludedPixels[2] = np.sort()
        pass

