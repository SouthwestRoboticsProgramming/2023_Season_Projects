from Detectors.Cargo import Cargo_Detector
import Constants
# TODO: Make an interface for all processes and detectors that would have a run and test

class Mono_Ball:
    stop = True
    print(Constants.gerald)
    #detector = None
    def run(self):
        detector = Cargo_Detector()
        while self.stop != False:
            print("Go")
            detector.periodic()


        print("Running Mono_Ball")
        return

    def stop(self):
        self.stop = True
        return