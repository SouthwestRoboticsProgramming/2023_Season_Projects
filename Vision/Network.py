from networktables import NetworkTable
from networktables import NetworkTablesInstance
from networktables import NetworkTableEntry

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
        table = NetworkTablesInstance.getDefault().getTable("bert")

        self.entry_isRedAlliance = table.getEntry("isRedAlliance")

        self.entry_runBallDetector = table.getEntry("run_ballDetector")
        pass


    def periodic(self):

        self.isRedAlliance = self.entry_isRedAlliance.getBoolean(True)

        self.runBallDetector = self.entry_runBallDetector.getBoolean(True)

        pass
    