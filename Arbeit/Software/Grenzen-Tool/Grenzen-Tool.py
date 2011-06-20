#© 2010 von Malte Karow

from tkinter import *
from tkinter import ttk


def coordfind(n): #gibt die Koordinaten der mit "n" Punkt/Linie aus PunkteLinien.kml zurück
    try:
        coord = open('./PunkteLinien.kml', 'r').read()
        for d in coord.split("</Placemark>"):
            if "<name>"+n+"</name>" in d:
                y = (d[d.find("<name>"+n+"</name>")+len("<name>"+n+"</name>"):])
        coord = y
        for item in coord.split("</coordinates>"):
            if "<coordinates>" in item:
                y = (item[item.find("<coordinates>")+len("<coordinates>"):])
                return y.strip('\n\t')
    except (ValueError, RuntimeError, TypeError, NameError):
        t.delete(1.0, 'end')
        t.insert(1.0, "Punkt/Linie "+ n +" existieren nicht")
        print("Fehler: Punkt/Linie "+ n +" existieren nicht")
                
def Liste(*args):            
    x=""    
    liste=eval(eingabe.get())
    for i in range(len(liste)): #For-Schleife um jeden Eintrag der Liste durchzugehen
        y=coordfind(str(liste[i])) #Funktion coordfind um Koordinaten des ersten Punktes zu bekommen
        x=x+" "+y
        if i+1 < len(liste):
            if liste[i] < liste[i+1]:
                y=coordfind(str(liste[i]) +"-"+str(liste[i+1])) #Funktion coordfind um Koordinaten der ersten Linie zu bekommen
                x=x+" "+y
            elif liste[i] > liste[i+1]:
                y=coordfind(str(liste[i+1]) +"-"+str(liste[i]))
                y=y.split()
                y.reverse()
                y=' '.join(y)
                x=x+" "+y
        elif i+1 == len(liste):
            if liste[i] < liste[0]:
                y=coordfind(str(liste[i]) +"-"+str(liste[0]))
                x=x+" "+y
                y=coordfind(str(liste[0]))
                x=(x+" "+y)
            elif liste[i] > liste[0]:
                y=coordfind(str(liste[0]) +"-"+str(liste[i]))
                y=y.split()
                y.reverse()
                y=' '.join(y)
                x=x+" "+y
                y=coordfind(str(liste[0]))
                x=x+" "+y
    t.delete(1.0, 'end')
    t.insert(1.0, x)


root = Tk()
root.title("Grenzen-Tool")

mainframe = ttk.Frame(root, padding="3 3 12 12")
mainframe.grid(column=0, row=0, sticky=(N, W, E, S))
mainframe.columnconfigure(0, weight=1)
mainframe.rowconfigure(0, weight=1)

eingabe = StringVar()

liste_eingabe = ttk.Entry(mainframe, width=40, textvariable=eingabe)
liste_eingabe.grid(column=2, row=1, sticky=(E))

ttk.Button(mainframe, text="Erstellen", command=Liste).grid(column=1, row=3, sticky=W)
ttk.Label(mainframe, text="Eingabe der Liste:").grid(column=1, row=1, sticky=W)
ttk.Label(mainframe, text="Ausgabe:").grid(column=1, row=2, sticky=(W, E))
ttk.Label(mainframe, text="© 2010 von Malte Karow").grid(column=2, row=3, sticky=(E))
t = Text(mainframe, width=40, height=10)
t.grid(column=2, row=2, sticky=(W, E))


for child in mainframe.winfo_children(): child.grid_configure(padx=5, pady=5)
liste_eingabe.focus()
root.bind('<Return>', Liste)

root.mainloop()
