import uuid
import os
import time
import cv2

IMAGES_PATH = os.path.join('yolov5', 'data', 'images') #/data/images
number_img = 50

# Create a webcam
cap = cv2.VideoCapture(0)

for img_num in range(number_img): # Loop through images
    time.sleep(2)
    
    print("Image: " + str(img_num))
    ret, frame = cap.read()

    imgname = os.path.join(IMAGES_PATH, 'powercell'+str(uuid.uuid1())+'.jpg')
    cv2.imwrite(imgname, frame)
    cv2.imshow("Image Collection", frame)

    if cv2.waitKey(10) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()

