from concurrent.futures import thread
from networktables import NetworkTables
import json

class Networking:
    # Note: in order to get a value from a table, the value must be put on the table first.

    num_threads = 1

    # Add final values here
    # ^TODO: REMOVE
    # IN #
    isRedAlliance = False
    # OUT #


    # Add entries here
    # IN #
    entry_isRedAlliance = None

    entry_thread = []

    entry_run_thread = []

    # OUT #

    
    def __init__(self):

        with open("general.json") as file:
            settings = json.load(file)

        self.num_threads = settings["threads"]
            
        NetworkTables.initialize()

        table = NetworkTables.getTable("bert")
        sb = NetworkTables.getTable("Shuffleboard/Vision")

        self.entry_thread = [None] * settings["threads"]
        self.entry_run_thread = [None] * settings["threads"]

        for i in range(settings["threads"]):
            #sb.putString("mode_thread" + str(i), "Nothing")
            #sb.putBoolean("run_thread" + str(i), True)

            sb.setPersistent("mode_thread" + str(i))
            sb.setPersistent("run_thread" + str(i))

            self.entry_thread[i] = sb.getEntry("mode_thread" + str(i))
            self.entry_run_thread[i] = sb.getEntry("run_thread" + str(i))

        self.entry_isRedAlliance = table.getEntry("isRedAlliance")
        self.sb = sb

        pass


    def periodic(self):

        self.isRedAlliance = self.entry_isRedAlliance.getBoolean(False)
        self.sb.setPersistent()
        pass
    