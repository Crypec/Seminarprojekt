importiere "Mathe".

// comments should be ignore by the lexer
definiere funktion test(x: Zahl) -> Text {
	variable x: Text = "test".
	konstante y: Zahl = 12,23.
	
	y := (x + 3) * 2.

	solange (x != 10) dann {
		x = x - 1-
	}

	rückgabe "bar".
}

definiere funktion Start() {
	  ausgabe("Das ist die Start Funktion").
	  test(12).
}
