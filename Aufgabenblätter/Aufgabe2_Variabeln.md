## Aufgaben 2: Variabeln

Dafür legen sie in Ihrer Entwicklungsumgebung ein neues Projekt mit dem Namen "Aufgabe 2" an.
In diesem Projekt dann eine neue Quelltextdatei mit dem Namen "Variabeln".

Bevor Sie anfangen ein paar wichtige Fachbegriffe die man später benötigt:

Deklarieren: Das bedeutet das man die Variable das erste mal "Erwähnt" ohne ihr einen Wert zu zuweisen. Dies ist bei STGI jedoch nicht erlaubt, denn man muss der Variable IMMER einen Wert zuweisen!<BR>
Initialisieren: Initialisieren bedeutet, das man die Variable Deklariert und ihr einen Wert zuweist.<BR>
````java
//Beispiel
n:zahl = 0 // Initialisierung!
n:zahl    // Deklaration, aber NICHT erlaubt!
````
Jetzt wissen Sie wie man Variabeln erzeugt, schreiben Sie nun folgendes Programmm:
````java
benutze[
    "IOTOOLS"
        ]

fun Start{
    
x: Zahl = 10
y: Zahl = 2

//Addition
Ausgabe ("{}", x + y )
Ausgabe ("{} + {}",x,y)

//Subtraktion
Ausgabe ("{}, x - y")
Ausgabe ("{} - {}", x, y)

}
````
Achten Sie vor allem darauf geöffnete Klammern auch wieder zu schließen! 
Kompilieren Sie und führen Sie nun das Programm aus.

####Aufgaben:<BR>
a) Was passiert beim Ausführen des Programms?<BR>
b) Was machen die einzelnen Ausgaben?<BR>
c) Vertauschen sie bei den Ausgaben die Variabeln hinter dem Komma!<BR>
d) Versuchen Sie auch andere Rechenoperatoren (*, /)<BR>
e) Was geschiet bei besonders großen Zahlen und sehr langen Nachkomastellen?<BR>
        -> Wie genau sind die Rechnungen unter dieser Bedingungen?<BR>
f) Versuchen Sie eine Eingabe wie folgt:<BR>
````
    Eingabe "Geben Sie einen Wert ein: "
    eingabe:= Eingabe("Gib eine Zahl ein: ")
````
g) // Eingabe unsicher!! MUSS NOCH GEÄNDERT WERDEN; Variable Eingabe muss noch einen Wert haben!
