import torch
from matplotlib import pyplot as plt
import numpy as np
import cv2

# Import YOLO v5
yolo = torch.hub.load('ultralytics/yolov5', 'custom', path='yolov5/runs/train/exp7/weights/last.pt', force_reload=True) # This uses the "medium" YOLO v5 option

cap = cv2.VideoCapture(0)
while cap.isOpened():
    ret, frame = cap.read()

    results = yolo(frame)

    cv2.imshow("YOLO", np.squeeze(results.render()))

    if cv2.waitKeyEx(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()