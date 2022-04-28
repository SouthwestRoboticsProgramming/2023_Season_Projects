# NOTE: To use opencv, use python 3.8
# TODO: The code
import cv2

from USBCamera import USBCamera

'''
    Object Structure:


'''

camera = USBCamera(0)
camera.useCalibration(True)
while True:
    cv2.imshow("Camera feed", camera.getFrame())
    cv2.waitKey(1)
