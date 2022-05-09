# NOTE: To use opencv, use python 3.8

from Thread import Thread, VisionMode

'''
    Thread Control:
    5 threads (List for variable threads?)
    In shuffleboard, have 5 strings, one for each thread
    Strings control which process, if invalid string, just do nothing
    When string changes, stop the thread and start another


'''

# Thread Constants #
numberOfThreads = 5

def main():

    threads = [None] * numberOfThreads

    for thread in threads:
        # Initialize thread
        thread = Thread(VisionMode.Nothing)
        pass

    # while True:
    #     network.periodic()
    threads[2] = Thread(VisionMode.Mono_Ball)
    pass

if __name__ == "__main__":
    main()