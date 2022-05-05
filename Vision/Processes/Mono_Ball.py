from Detectors.Cargo import Cargo_Detector
from USBCamera import USBCamera
import Constants
import cv2
# TODO: Make an interface for all processes and detectors that would have a run and test

class Mono_Ball:

    stop = True

    def run(self):

        detector = Cargo_Detector(Constants.fov_ballCamera)
        camera = USBCamera(Constants.id_ballCamera)

        while self.stop != False:
            frame = camera.getFrame()
            detector.periodic(frame)

            #detector.setValue(5,250)
            detector.setHue(350, 5)

            cv2.imshow("Result", detector.getMaskedFrame())
            cv2.waitKey(1)


        print("Running Mono_Ball")
        return

    def stop(self):
        self.stop = True
        return