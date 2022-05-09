from asyncio import constants
from Detectors.Cargo import Cargo_Detector
from USBCamera import USBCamera
import Constants
import cv2
from enum import Enum
# TODO: Make an interface for all processes and detectors that would have a run and test
# TODO: Be able to change ball color during the match


class Mono_Ball:

    stop = True

    def run(self, network, startEntry):

        detector = Cargo_Detector(Constants.fov_ballCamera)
        camera = USBCamera(Constants.id_ballCamera)

        hue = (0,255)
        sat = (0,255)
        value = (0,255)

        isRedAlliance = network.isRedAlliance

        if isRedAlliance == False:
            hue = Constants.blue_hue
            sat = Constants.blue_sat
            value = Constants.blue_val
        elif isRedAlliance == False:
            hue = Constants.red_hue
            sat = Constants.red_sat
            value = Constants.red_val

        while self.stop and startEntry.getBoolean(True):
            frame = camera.getFrame()
            detector.periodic(frame)

            #detector.setValue(5,250)
            detector.setHue(hue[0],hue[1])
            detector.setSat(sat[0],sat[1])
            detector.setValue(value[0],value[1])

            cv2.imshow("Result", detector.getMaskedFrame())
            print(detector.getAngles()[0])
            cv2.waitKey(1)


        print("Running Mono_Ball")
        return

    def stop(self):
        self.stop = True
        return

    # TODO: Red and blue mode with settings
