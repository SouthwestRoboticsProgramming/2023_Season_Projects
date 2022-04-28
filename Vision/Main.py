# NOTE: To use opencv, use python 3.8

import cv2
import threading
from enum import Enum

from USBCamera import USBCamera

'''
    On the java side:
    - Create threads with modes
    - Start and stop threads
    - Control masking


'''
def main():
    camera = USBCamera(0)
    camera.useCalibration(True)
    while True:
        cv2.imshow("Camera feed", camera.getFrame())
        cv2.waitKey(1)

if __name__ == "__main__":
    main()