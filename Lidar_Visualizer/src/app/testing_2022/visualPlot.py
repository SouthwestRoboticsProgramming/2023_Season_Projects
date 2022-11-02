from random import random
from tkinter import Tk, Canvas, Frame, BOTH


class visualPlot:

    main = None

    canvas = None

    master = None

    scaler = 1.5

    def __init__(self, main):
        self.main = main

        def callback(event):
            main.robotPos = [event.y-10, event.x-10, (random()*360)-180]
            print ("clicked at " + str(event.x), str(event.y))
            print("\n\n\n\n\n")
            main.run()
            
        
        self.master = Tk()
    
        self.master.title("Lines")
        self.master.geometry("400x250")
        self.canvas = Canvas(self.master)
        self.canvas.bind("<Button-1>", callback)

    def loadPlot(self, fieldPlot, setRobotPos, foundRobotPos):
        self.canvas.delete('all')
        for i in fieldPlot:
            self.canvas.create_line(round(i[1]*self.scaler)+10, round(i[0]*self.scaler)+10, round(i[3]*self.scaler)+10, round(i[2]*self.scaler)+10)
        for i in setRobotPos:
            self.canvas.create_line(round(i[1]*self.scaler)+10, round(i[0]*self.scaler)+10, round(i[3]*self.scaler)+10, round(i[2]*self.scaler)+10, fill='grey')
        for i in foundRobotPos:
            self.canvas.create_line(round(i[1]*self.scaler)+10, round(i[0]*self.scaler)+10, round(i[3]*self.scaler)+10, round(i[2]*self.scaler)+10, fill='blue')
        self.canvas.pack(fill=BOTH, expand=1)
        print("loaded plot")
        self.master.mainloop()
        print("Closed Plot")


# def main():

#     # root = Tk()
#     ex = visualPlot(None)
#     ex.loadPlot([[0,0,400,200]],[])
#     # root.geometry("400x250+300+300")
#     # root.mainloop()


# if __name__ == '__main__':
#     main()