# NOTE: To use opencv, use python 3.8

from Thread import Thread, VisionMode
import json

'''
    Thread Control:
    5 threads (List for variable threads?)
    In shuffleboard, have 5 strings, one for each thread
    Strings control which process, if invalid string, just do nothing
    When string changes, stop the thread and start another


'''

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
    threads[2] = Thread(2, VisionMode.Mono_Ball)
    pass

if __name__ == "__main__":
    main()