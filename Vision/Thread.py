import threading
from enum import Enum
from Main import VisionMode

class Thread:
    def __init__(self, visionMode):
        if visionMode == VisionMode.Mono_Ball:
            print("Mono Mode")
        return


