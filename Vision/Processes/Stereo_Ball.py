from Detectors.Cargo import Cargo_Detector
from USBCamera import USBCamera
from Stereo_Helper import Stereo_Helper
import json
import cv2

class Stereo_Ball:
    # Find x angle and distance to a ball
    
    run = True

    def run(self, network, enable):
        
        # Open the JSON file with all of the settings for the ball camera
        with open("ball_stereo.json") as file:
            settings = json.load(file)
            
        detector_l = Cargo_Detector(settings["camera"]["fov"])
        detector_r = Cargo_Detector(settings["camera"]["fov"])
        camera_l = USBCamera(settings["camera"]["id_l"])
        camera_r = USBCamera(settings["camera"]["id_r"])

        helper = Stereo_Helper(settings["camera"]["base"])

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
                frame_l = camera_l.getFrame()
                frame_r = camera_r.getFrame()
                detector_l.periodic(frame_l)
                detector_r.periodic(frame_r)

                #detector.setValue(5,250)
                detector_l.setHue(hue[0],hue[1])
                detector_l.setSat(sat[0],sat[1])
                detector_l.setValue(value[0],value[1])

                detector_r.setHue(hue[0],hue[1])
                detector_r.setSat(sat[0],sat[1])
                detector_r.setValue(value[0],value[1])

                angles_l = detector_l.getAngles()
                angles_r = detector_r.getAngles()

                x, y = helper.solveStereo(angles_l[0],angles_r[0])

                centerAngle = angles_r[0] - angles_l[0]

                # Return values
                # return y, centerAngle

                # TODO: Remove
                # cv2.imshow("Result", detector.getMaskedFrame())
                # print(detector.getAngles()[0])
                # cv2.waitKey(1)


        print("Running Mono_Ball")
        return
