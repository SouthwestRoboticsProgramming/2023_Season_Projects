from networktables import NetworkTable
from networktables import NetworkTablesInstance
from networktables import NetworkTableEntry
from networktables import NetworkTables
import sys
import logging

class Networking:

    # Add final values here
    # IN #
    isRedAlliance = False

    runBallDetector = False

    # OUT #


    # Add entries here
    # IN #
    entry_isRedAlliance = None

    entry_runBallDetector = None

    # OUT #

    
    def __init__(self):
        logging.basicConfig(level=logging.DEBUG)

        NetworkTables.initialize()
        table = NetworkTables.getTable("bert")


        self.entry_isRedAlliance = table.getEntry("isRedAlliance")

        self.entry_runBallDetector = table.getEntry("run_ballDetector")

        pass


    def periodic(self):

        self.isRedAlliance = self.entry_isRedAlliance.getBoolean(True)

        self.runBallDetector = self.entry_runBallDetector.getBoolean(True)

        pass
    