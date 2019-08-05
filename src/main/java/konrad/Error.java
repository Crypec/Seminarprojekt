package konrad;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;


public class Error {
    public static void report(String msg, SourceFile source) {
	String output = "@|red,bold " + msg + "|@";
	System.out.println(">");

	System.out.printf("> %s %n", ansi().render(output));
	System.out.println(">");
    }
}
