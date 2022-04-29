import threading
from networktables import NetworkTables
from enum import Enum


class VisionMode(Enum):
    # All of the modes that vision has for various FRC use cases
    Mono_Target = "Mono Target"
    Stereo_Target = "Stereo Target"

    Mono_Object = "Mono Object"
    Stereo_Object = "Stereo Object"

    Mono_Ball = "Mono Ball"
    Stereo_Ball = "Stereo Ball"


class Thread:
    def __init__(self, visionMode):
        if visionMode == VisionMode.Mono_Target:
            # threading.Thread
            print("Mono Target")

        elif visionMode == VisionMode.Stereo_Target:
            print("Stereo Target")
        
        elif visionMode == VisionMode.Mono_Object:
            print("Mono Object")
        
        elif visionMode == VisionMode.Stereo_Object:
            print("Stereo Object")

        elif visionMode == VisionMode.Mono_Ball:
            print("Mono Ball")

        elif visionMode == VisionMode.Stereo_Ball:
            print("Stereo Ball")
        
        else:
            print("Not a valid mode")


        return


