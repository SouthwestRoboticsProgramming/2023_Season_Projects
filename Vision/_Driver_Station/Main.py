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

    cv2.namedWindow("Name")
    pixelGetter = Pixel_Getter("Name")

    while(1):
        print('({}, {})'.format(pixelGetter.mouseX, pixelGetter.mouseY))
        cv2.waitKey(1)
    pass

if __name__ == "__main__":
    main()