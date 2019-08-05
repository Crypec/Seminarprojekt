importiere "Mathe".
importiere "Ausgabe".

/*
	Support for multiline comments -> check in pre-processor
	Support for german boolean operators?

	solange (x gleich 10) dann {

	}
*/


// support for inline comments
// this comment describes the function below

definiere funktion test(var x: Zahl) -> Text {

	  wenn (x > 10) dann {
	       	  rückgabe "test".
	  }

	  // add support for Linter -> naming convention
	  variable FooBar: Text = "test".
	  variable foo_bar: Text = "test".

	  // support for external namespaces
	  definiere variable y: Zahl = Mathe::sin(x).

	  // eventual support for automatic type annotation 
	  test := 12.
	  
	  variable foo: Text = "bar".

	  solange (x != 0) dann {
	  	  ausgabe("das ist ein Text der Schleife").
		  x := x -1.

		  //support for shorthand notations
		  --x.
		  x--.
	  }
	  
	  test: Wahrheitswert = A UND B.
	  
	  solange (x ungleich 10) dann {
	  	  
	  }

	  solange (x != 10) {
	  	  ausgabe("Hello World").
	  }


	  // while(true)
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

// the generated cpp code for parts of the above code example
std::string test(double zahl) {
	    if (x > 10) {
	       	  return "test";
	    }
	    double y = Math::sin(x); 
	    while (x != 0) {
	    	  fmt::print("this is a text which gets printed in a loop");
		  x -= 1;
	    }
	    return "this is the return";
}