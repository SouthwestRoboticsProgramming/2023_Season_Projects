# NOTE: To use opencv, use python 3.8

import cv2
import threading
from enum import Enum
from Thread import Thread, VisionMode
from networktables import NetworkTables

from USBCamera import USBCamera

'''
    Thread Control:
    5 threads (List for variable threads?)
    In shuffleboard, have 5 strings, one for each thread
    Strings control which process, if invalid string, just do nothing
    When string changes, stop the thread and start another


'''
def main():
    NetworkTables.initialize()
    thread = Thread(VisionMode.Mono_Ball)

if __name__ == "__main__":
    main()