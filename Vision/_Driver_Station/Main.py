from cv2 import COLOR_BGR2HSV
from Mouse import Pixel_Getter
import cv2


'''
What should the driver station do:
- Include pixels using left click
- Ignore pixels using right click
- Change clump size using scroll wheel
- Put it on networktables

'''

def main():

    # TODO: Remove later
    cap = cv2.VideoCapture(0)

    cv2.namedWindow("Camera Feed")
    pixelGetter = Pixel_Getter("Camera Feed")

    while(1):
        ret, frame = cap.read()

        frame = cv2.cvtColor(frame, COLOR_BGR2HSV)
        pixelGetter.setFrame(frame)

        cv2.imshow("Camera Feed", pixelGetter.getFrame())
        cv2.waitKey(1)
    pass

if __name__ == "__main__":
    main()