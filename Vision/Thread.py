import threading
from networktables import NetworkTables
from enum import Enum
from Processes import *
from Processes.Mono_Ball import Mono_Ball
from Network import Networking
from Processes.Stereo_Ball import Stereo_Ball
from Processes.Nothing import Nothing


class VisionMode(Enum):
    # All of the modes that vision has for various FRC use cases
    Mono_Target = "Mono Target"
    Stereo_Target = "Stereo Target"

    Mono_Object = "Mono Object"
    Stereo_Object = "Stereo Object"

    Mono_Ball = "Mono Ball"
    Stereo_Ball = "Stereo Ball"

    Nothing = "Nothing"

    thread = None

    network = None


class Thread:

    def __init__(self, threadNum):

        self.network = Networking()
        self.network.entry_thread[threadNum].setString("Thing")
        visionMode = self.network.entry_run_thread[threadNum].getString("Nothing")

        print(visionMode)

        if visionMode == VisionMode.Mono_Target.value or visionMode == VisionMode.Mono_Target:
            print("Mono Target")

        elif visionMode == VisionMode.Stereo_Target.value or visionMode == VisionMode.Stereo_Target:
            print("Stereo Target")
        
        elif visionMode == VisionMode.Mono_Object.value or visionMode == VisionMode.Mono_Object:
            print("Mono Object")
        
        elif visionMode == VisionMode.Stereo_Object.value or visionMode == VisionMode.Stereo_Object:
            print("Stereo Object")

        elif visionMode == VisionMode.Mono_Ball.value or visionMode == VisionMode.Mono_Ball:
            print("Mono ball")
            self.thread = threading.Thread(target=Mono_Ball.run, args=(Mono_Ball(),self.network,self.network.entry_run_thread[threadNum]))
            self.thread.start()

        elif visionMode == VisionMode.Stereo_Ball.value or visionMode == VisionMode.Stereo_Ball:
            self.thread = threading.Thread(target=Stereo_Ball.run, args=(Stereo_Ball(),self.network,self.network.entry_run_thread[threadNum]))
            self.thread.start()
        
        elif visionMode == VisionMode.Nothing.value or visionMode == VisionMode.Nothing:
            print("Doing nothing")
            self.thread = threading.Thread(target=Nothing.run)
            self.thread.start()

        else:
            print("Not a valid mode")


        return


    def stop(self):
        self.thread.join()
        return


