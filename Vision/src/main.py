import logging
from networktables import NetworkTables

# Get field data to improve logging
fms = NetworkTables.getTable("FMSInfo")
match_type = fms.getEntry('MatchType')
match_number = fms.getEntry('MatchNumber')

# Configure logging first so other files can use the same formatting
LOG_FILENAME = 'Tracker.Log'
TIME_FORMAT = f"%y %a %b {match_type.getString('Test Match')} {match_number.getString('0')}"

LOG_FORMAT = '%(levelname)s %(asctime)s - %(message)s'
logging.basicConfig(filename=LOG_FILENAME,
                    level=logging.DEBUG,
                    format=LOG_FORMAT,
                    datefmt=TIME_FORMAT)
logger = logging.getLogger()


from argparse import ArgumentParser
from shufflelog_api import ShuffleLogAPI
from cameras import *
import json


def main():  # sourcery skip: use-named-expression
    # Create a parser to allow variable arguments
    parser = ArgumentParser(prog='Vision for FRC',
                            description='Vision for FRC')
    parser.add_argument('-i', '--networktable_ip', type=str, default='localhost', metavar='', help='RoboRIO ip to access network tables')
    parser.add_argument('-c', '--cameras', type=str, default='cameras.json', metavar='', help='Path to camera definition JSON')
    parser.add_argument('-n', '--no_gui',  action='store_true', help='Hide OpenCV gui.')

    args = parser.parse_args()

    # Configure NetworkTables
    networktable_ip = args.networktable_ip
    if networktable_ip:  # If valid ip
        NetworkTables.initialize(server=networktable_ip)
    else:
        NetworkTables.initialize()
    # Tables to send back to RoboRIO and driver station
    table = NetworkTables.getTable("Object Vision")  # TODO: Give it a name

    # Exctract cameras JSON
    try:
        cameras_json = open(args.cameras, 'r')
        cameras = json.load(cameras_json)
        logger.info("Cameras JSON loaded")
    except (FileNotFoundError, json.JSONDecodeError) as e:
        logger.exception("Could not open cameras JSON, quitting")
        raise FileNotFoundError(f"Could not open cameras JSON '{args.cameras}', is the path relative to /TagTracker?") from e
    cameras_json.close()


    camera_list = [Camera(camera_info) for camera_info in cameras['cameras']]

    # Setup a camera array with the JSON settings
    camera_array = CameraArray(logger, camera_list)

    # Initialize ShuffleLog API
    messenger_params = {
        'host': 'localhost',
        'port': 5805,
        'name': 'Object Vision',
        'mute_errors': True
    }
    # api = ShuffleLogAPI(messenger_params, environment['tags'], cameras['cameras'])

    # Main loop, run all the time like limelight
    while True:
        data = camera_array.read_cameras()

        if not args.no_gui:
            for i, image in enumerate(data):
                cv2.imshow(str(i), image['image'])

        # Send the solved position back to robot
        # api.publish_test_matrices(matrices)

        # Read incoming API messages
        # api.read()

        # Q to stop the program
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    # Disconnect from Messenger
    # api.shutdown()


if __name__ == '__main__':
    main()
