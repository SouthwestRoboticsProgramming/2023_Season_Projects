import cv2
import numpy

class USBCamera:

    '''
    Requirements:

    - Get an image
    - Get a calibrated image
    - Check that the camera can be created
    - Get camera ID
    '''

    cameraID = 0

    capture = None

    def __init__(self, cameraID):
        self.cameraID = cameraID
        self.capture = cv2.VideoCapture(cameraID)
        break

    def check():
        # Checks if the camera works
        ret, frame = self.capture.read()
        return ret
    