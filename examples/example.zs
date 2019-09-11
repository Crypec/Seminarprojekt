importiere "Mathe". //vllt wäre "benutze" das bessere Wort
importiere "Ausgabe".
test


benutze "Mathe".



	// support for inline comments
	// this comment describes the function below

definiere funktion test(x: Zahl) -> Text {

	  wenn (x > 10) dann {
	       	  rückgabe "test".
	  }

	  // add support for Linter -> naming convention
	  variable FooBar: Text = "test".
	  variable foo_bar: Text = "test".

	  // support for external namespaces
	  definiere variable y: Zahl = Mathe::sin(x).

	  solange (wahr) {
		  ausgabe("test")
		  test := eingabe("test")
	  }
	  
	  // eventual support for automatic type annotation 
	test := 12.
		
	variable foo: Text = "bar".
	test := a + 1000_2.
	foo := 100 000_23.
	

	solange (x != 0) dann {
		ausgabe("das ist ein Text der Schleife").
			x := x -1.
			test.
				//support for shorthand notations
				--x.
				x--.
			}
	  
	  test: Wahrheitswert = A UND B.
	  
	  solange (x ungleich 10) dann {
	  	  
	  }

	  solange (x != 10) {
	  	  ausgabe("Hello World"). // should we do print and input as syscalls
	  }

	  fÜr i := 10 bis -10 {
	      		 
	  }


	  solange (x UNGLEICH 10) {
	  	  eingabe("test").
          }

	  // while(true)
	  solange (wahr)
	  schleife {
	  	   weiter.
	  }

	  wiederhole {
	  	     wenn ((x modulo 2) == 0) {
		     	  weiter. //continue
		     } sonst {
		       	  stop. // break
		     }
	  } 

	  rückgabe "das ist der return".

	  }
}
