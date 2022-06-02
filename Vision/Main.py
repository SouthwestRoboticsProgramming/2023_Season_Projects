# NOTE: To use opencv, use python 3.8

from Thread import Thread, VisionMode
import json

# TODO: Fix issue where if nothing is running, the program just stops

def main():
    with open("general.json") as file:
        settings = json.load(file)
            

    threads = [None] * settings["threads"]

    for thread in enumerate(threads):
        # Initialize thread
        thread = list(thread)
        thread[1] = Thread(thread[0])
        pass

    # while True:
    #     network.periodic()
    pass

if __name__ == "__main__":
    main()