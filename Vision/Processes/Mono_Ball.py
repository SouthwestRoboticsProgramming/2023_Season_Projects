from Detectors.Cargo import Cargo_Detector
from USBCamera import USBCamera
import json
import cv2
# TODO: Be able to change ball color during the match


class Mono_Ball:


    run = True

    def run(self, network, enable):

        # Open the JSON file with all of the settings for the ball camera
        with open("ball.json") as file:
            settings = json.load(file)
            
        detector = Cargo_Detector(settings["camera"]["fov"])
        camera = USBCamera(settings["camera"]["id"])

        hue = (0,255)
        sat = (0,255)
        value = (0,255)

        isRedAlliance = network.isRedAlliance

        if isRedAlliance == False:
            hue = settings["red"]["hue"]
            sat = settings["red"]["sat"]
            value = settings["red"]["val"]
        elif isRedAlliance == False:
            hue = settings["blue"]["hue"]
            sat = settings["blue"]["sat"]
            value = settings["blue"]["val"]

        while self.run:
            if enable.getBoolean(True):
                frame = camera.getFrame()
                detector.periodic(frame)

                #detector.setValue(5,250)
                detector.setHue(hue[0],hue[1])
                detector.setSat(sat[0],sat[1])
                detector.setValue(value[0],value[1])

                # TODO: Remove
                cv2.imshow("Result", detector.getMaskedFrame())
                print(detector.getAngles()[0])
                cv2.waitKey(1)


        print("Running Mono_Ball")
        return

    def stop(self):
        self.run = False
        # TODO: Change the shuffleboard entries
        return
