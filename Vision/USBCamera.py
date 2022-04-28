import cv2
import numpy as np
import glob

class USBCamera:

    cameraID = 0
    capture = None

    useCalibration = False
    calibrationProfile = None


    def __init__(self, cameraID):
        self.cameraID = cameraID
        self.capture = cv2.VideoCapture(cameraID)

    def check(self):
        # Checks if the camera works
        ret, frame = self.capture.read()
        return ret

    def useCalibration(self, useCalibration):
        self.useCalibration = useCalibration

    def setCalibrationImage(self, calibrationImage, checkerboard):
        # ** Checkerboard should be a list ** #
        self.calibrationProfile = Calibration(calibrationImage, checkerboard)


    def getCameraID(self):
        return self.cameraID
    def getFrame(self):

        ret, frame = self.capture.read()
        if self.useCalibration and self.calibrationProfile is not None:
            frame = self.calibrationProfile.undistortImage(frame)
        return frame

    def getRawFrame(self):
        ret, frame = self.capture.read()
        return frame




class Calibration:

    calibrationProfile = None
    calibrationSuccess = False

    mtx = None
    dist = None
    newCameraMtx = None
    roi = None

    def __init__(self, calibrationImage, checkerboardDimensions):

        # ** Checkerboard dimensions should be a list ** #
        
        criteria = (cv2.TermCriteria_EPS + cv2.TermCriteria_MAX_ITER, 30, 0.001)

        objpoints = []
        # Vector to store 2D points
        imgpoints = []

        objp = np.zeros((1, checkerboardDimensions[0] * checkerboardDimensions[1], 3), np.float32)
        objp[0,:,:2] = np.mgrid[0:checkerboardDimensions[0],0:checkerboardDimensions[1]].T.reshape(-1,2)

        images = glob.glob(calibrationImage)

        for fname in images:
            img = cv2.imread(fname)
            if img is not None:
                gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
                # Find the corners
                ret, corners = cv2.findChessboardCorners(gray,checkerboardDimensions,cv2.CALIB_CB_ADAPTIVE_THRESH + cv2.CALIB_CB_FAST_CHECK + cv2.CALIB_CB_NORMALIZE_IMAGE)
                # If criterion is met, refine the corners
                if ret:
                    objpoints.append(objp)
                    corners2 = cv2.cornerSubPix(gray, corners, (11,11),(-1,-1),criteria)

                    imgpoints.append(corners2)
                    

                    img = cv2.drawChessboardCorners(img, checkerboardDimensions,corners2,ret)

                    self.calibrationSuccess = True
                else:
                    print("No corners found")
                    self.calibrationSuccess = False
            else:
                print("No image found")
                self.calibrationSuccess = False
                

        h,w = img.shape[:2]

        ret, mtx, dist, rvecs, tvecs = cv2.calibrateCamera(objpoints, imgpoints, gray.shape[::-1], None, None)

        newcameramtx, roi = cv2.getOptimalNewCameraMatrix(mtx, dist, (w,h), 1, (w,h))

        # dst = cv2.undistort(img,mtx,dist,None,newcameramtx)

        # x,y,w,h = roi
        # dst = dst[y:y+h,x:x+w]

        self.mtx = mtx
        self.dist = dist
        self.newCameraMtx = newcameramtx
        self.roi = roi



    def undistortImage(self, img):

        if not self.calibrationSuccess:
            return img

        undistortedFrame = cv2.undistort(img,self.mtx,self.dist,None,self.newCameraMtx)

        x,y,w,h = self.roi
        undistortedFrame = undistortedFrame[y:y+h,x:x+w]

        return undistortedFrame
    