importiere "mathe"

fun add(a: Text, b: Zahl, istFalsch: Bool) -> Zahl {

    wenn istFalsch {
    	 solange a > b {
	    a = a +1 
	 }
    } sonst {
      	 rueckgabe 2390
    }
    rueckgabe 2398,2389
}


fun abs(x: Zahl) -> Zahl {

    wenn x > 0 {
    	 rueckgabe x 
    } sonst {
      	 rueckgabe -1 * x  
    }
}

Typ SpielBrett {
    adresse: Text,
    nummmer: Zahl,
}

implementiere Haus {
    fun neu(addr: Text, nr: Zahl) -> Haus {
    	rueckgabe Haus{
	    adresse: addr,
	    nummer: nr,
	}		  
    }


    fun formatiere(selbst) -> Text {
    }
}

fun Start() {

    brett : [[Zahl]] = [[0, 0, 0, 0, 0], [0, 0, 0, 0, 0]];

    brett := Liste::neu()

    wenn brett.laenge > 0 {
    	 Fmt::ausgabe();
    }

    user := eingabe("was ist dein begrehren, welche zahl willst du haben")
    fmt::ausgabe("Der absolutwert deiner Zahl ist {}", abs(eingabe))
    sinx := mathe::sin(x)



}