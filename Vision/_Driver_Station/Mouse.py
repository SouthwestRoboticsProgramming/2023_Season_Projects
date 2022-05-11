import cv2

class Pixel_Getter:

    mouseX = 0
    mouseY = 0

    includedPixels = []
    excludedPixels = []

    def _updateMouse(self, event, x, y, flags, param):

        # Only update the mouse position if it is moving
        if event == cv2.EVENT_MOUSEMOVE:
            self.mouseX = x
            self.mouseY = y

        if event == cv2.EVENT_LBUTTONDOWN:
            # Get the pixel cluster and add it to the list of included pixels
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

