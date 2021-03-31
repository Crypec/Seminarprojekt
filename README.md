# Stegi 

Kuzoto ist eine Programmiersprache speziell fuer Programmieranfanger. Ihr Ziel ist es den Start ins Programmieren moeglichst einfach zu gestalten.

# Tutorial

## Und am Anfang war START
Jedes Programm in Kuzoto beginnt bei der Start funktion. Sie muss immer folgendermassen aussehen, wenn du wissen willst was eine genau eine Funktion ist schau doch einfach hier nach :D.
```go
fun Start() {
	fmt::Ausgabe("Hallo Welt")
	name := fmt::Eingabe("Wie ist dein name: ")
	fmt::Ausgabe("Hallo {}", name)
}
```	
## Variablen	
Eine Variable ist ein Behaelter fuer etwas was du speichern willst. Du kannst ihr Werte zuweisen und diese spaeter abrufen.
```go
pi := 3.14159 //Du speicherst also den Wert pi in der Variable mit dem Namen pi
r := 5
umfang := 2 * pi * r // hier wird also der Umfang eines Kreises mit dem Radius 5 berechnet!

fmt::Ausgabe(umfang)
```	
