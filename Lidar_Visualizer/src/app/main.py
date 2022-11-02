from lidar_lib.rplidar import RPLidar


class lidarProject:

    class Exception(Exception):
        '''Exception class for Lidar Project'''

    lidarSpec = {'serial':'/dev/ttyUSB0', 'firmware': (1, 29), 'hardware': 7, 'model': 24, 'health': 'Good'}

    def __init__(self):
        self.lidar = RPLidar('/dev/ttyUSB0')
        if (self.checkLidar()):
            self.startWebServer()
        else: 
            print("Exiting")
            exit()
        pass

    def checkLidar(self):
        info = self.lidar.get_info()
        health = self.lidar.get_health()
        if (    info['firmware'] == self.lidarSpec['firmware'] and
                info['hardware'] == self.lidarSpec['hardware'] and
                info['model'] == self.lidarSpec['model'] and
                health[0] == self.lidarSpec['health']):
            print("Lidar is ready")
            return True
        else:
            raise Exception("Lidar not ready")
            print('Lidar Info: ' + info)
            print('Lidar Health: ' + health)
            return False

    def startWebServer(self):
        pass
        

    

lidarProject = lidarProject()
lidarProject.checkLidar()


# dictEx = {'firmware': (1, 29), 'hardware': 7, 'model': 24, 'serialnumber': 'BFAF99F6C9E59AD5C5E59CF7437D3415'}
# print(str(dictEx["firmware"] == (1, 29)))

# tupEx = ('Good', 0)
# print(tupEx[0])