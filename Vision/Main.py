# NOTE: To use opencv, use python 3.8

from Thread import Thread, VisionMode
import json

# TODO: Fix issue where if nothing is running, the program just stops

def main():

    with open("general.json") as file:
        settings = json.load(file)
            

    threads = [None] * settings["threads"]

    for thread in threads:
        # Initialize thread
        thread = Thread(thread, VisionMode.Nothing)
        pass

    # while True:
    #     network.periodic()
    threads[2] = Thread(2, VisionMode.Stereo_Ball)
    pass

if __name__ == "__main__":
    main()