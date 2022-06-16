import torch
from matplotlib import pyplot as plt
import numpy as np
import cv2

# Import YOLO v5
yolo = torch.hub.load('ultralytics/yolov5', 'yolov5m') # This uses the "medium" YOLO v5 option

