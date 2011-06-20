#© 2010 von Malte Karow

x=""

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
        print("Punkt/Linie"+ n +"existieren nicht")
                
            
    
liste=eval(input("Liste eingeben:"))
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

        
