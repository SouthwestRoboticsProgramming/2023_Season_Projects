# NOTE: To use opencv, use python 3.8
import cv2
import threading
from enum import Enum
from Thread import Thread, VisionMode
from Network import Networking
import timeit
import time

from USBCamera import USBCamera

'''
    Thread Control:
    5 threads (List for variable threads?)
    In shuffleboard, have 5 strings, one for each thread
    Strings control which process, if invalid string, just do nothing
    When string changes, stop the thread and start another


'''

# Thread Constants #
numberOfThreads = 5

def main():

    threads = [None] * numberOfThreads

    for i in threads:
        # Initialize thread
        threads[i] = Thread(VisionMode.Nothing)
        pass

    # while True:
    #     network.periodic()
    thread = Thread(VisionMode.Mono_Ball)
    thread.run()
    pass

if __name__ == "__main__":
    main()