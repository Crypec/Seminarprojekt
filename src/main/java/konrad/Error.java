package konrad;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;


public class Error {

    //TODO(Simon): show line where error has occured
    public static void reportError(String msg, SourceFile source) {
	String output = "@|red,bold " + msg + "|@";
	System.out.println(">");
	System.out.printf("> %s %n", ansi().render(output));
	System.out.println(">");
    }

    public static void reportWarning(String errorType, String msg, String description, SourceFile source) {
	System.out.printf("> Zuse Kompiler Warnung: %s %n", errorType);

	String output = "@|yellow,bold " + msg + "|@";
	System.out.println(">");
	System.out.printf("> %s %n", ansi().render(output));
	System.out.println(">");

	//@TODO(Simon): printing in parts is broken. Indentation of fist line is messed up relative to the rest.
	//@TODO(Simon): i strongly suspect it has something to do with the splitting of the Description
	System.out.println();
	System.out.println(ansi().render("> @|bold Beschreibung |@"));
	System.out.println();
	String[] parts = description.split("\\.");
	for (String part : parts) {
	    System.out.printf("> %s. %n", part);
	}
    }
}
