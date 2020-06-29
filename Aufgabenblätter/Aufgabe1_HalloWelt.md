## Aufgabe 1: Hallo Welt
Das Programm "Hallo Welt" ist ein sehr einfaches Programm, welches zeigen soll wie die Programmiersprache STGI aufgebaut ist. Dieses Programm soll lediglich erstmal die Worte "Hallo Welt" ausgeben :)

Dafür legen sie in Ihrer Entwicklungsumgebung ein neues Projekt mit dem Namen "Aufgabe 1" an.
In diesem Projekt dann eine neue Quelltextdatei mit dem Namen "HalloWelt".

Schreiben Sie nun folgendes Programm: 
````
benutze
[
 "IOTools"
]
fun Start {
ausgabe "Hallo Welt!"
}
````
Achten Sie vor allem darauf geöffnete Klammern auch wieder zu schließen! 
Kompilieren Sie und führen Sie nun das Programm aus.

####Aufgaben: <BR> 
Diese Aufgaben sollen Ihnen nicht nur helfen, die Grundlagen besser zuverstehen sondern sie auch dazu bringen zu Experimentieren. Denn das ist nicht nur erlaubt, es ist ein muss um Fortschritte zu machen.

Wenn Sie Sachen in Ihrem Programm mit Notizen versehen wollen, können sie die folgende Klammern benutzen:<BR> //Dies ist ein Kommentar

a) Was geschiet beim Ausführen des Programms?<BR>
b) Bauen sie weitere Ausgaben ein, was passiert, wenn Sie die hochgestellten Anführungszeichen  weglassen?  
c) Was passiert wenn sie eine Rechnung in hochgestellten Anführungszeichen schreiben? Was passiert wenn Sie diese weglassen? <BR>
z.B: 
````java
benutze
[
 "IOTools"
]
start
ausgabe "Hallo Welt!"
ausgabe "3+10" // mit hochgestellten Anführungszeichen
ausgabe "{}", 3+10 //ohne hochgestellte Anführungszeichen
[
````
d) Was ist der Unterschied zwischen Textausgaben und Zahlen/Rechnungen?<BR>
e) Wo braucht man die hochgestellten Anführungszeichen, wovon hängt es ab?(Bei welchen Typen sind sie notwendig?)<BR>
f) Erzeugen Sie (Initialisiere) eine Variable wie unten gezeigt, gebe Sie dann wie folgt aus:
````java
benutze
[
 "IOTools"
]
fun Start{

a: Zahl = 2
Ausgabe "a hat den Wert {}!", a

}
````
Was passiert wenn Sie das Programm ausführen? Versuchen Sie es mit anderen Beispielen und mehreren Variablen!

Wenn Sie die Basics nochmal nachlesen wollen folgen Sie folgendem Link: 
"LINK PLATZHALTER"
