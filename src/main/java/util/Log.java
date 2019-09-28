package konrad.util;

import konrad.util.*;
import com.github.tomaslanger.chalk.*;

public class Log {

    public static void reportWarning(String errorType, String msg)  {
	System.out.printf("%s :: %s:  %n%s%n", Chalk.on("WARNUNG").yellow().bold().underline(), Chalk.on(errorType).yellow().bold(), msg);
	System.out.println();
    }

    public static void printErrorHeader(String errorType, MetaData meta) {
	System.out.printf("%s :: %s%n", Chalk.on("ERROR").red().bold().underline(), errorType);
	System.out.printf("%nFehler in Datei %s [Zeile: %d :: %d]!", meta.filename, meta.line, meta.colum);
    }
    public static void invalidTokenError(MetaData meta) {

	printErrorHeader("Invalider Token -> [ExprParser]", meta);

	System.exit(1);
    }

    public static void printFileLine(MetaData meta) {
    }
    
    public static void funcDeclArgumentTypeError(MetaData meta) {
	printErrorHeader("TypenError -> [FuncDeclParser]", meta);
	printFileLine(meta);
	System.out.println("\nEine Funktion in Zuse folgt folgendem Bauplan:");
	System.out.printf("%nfun name(argument: %s) -> %s {} %n", Chalk.on("TypenName").yellow().bold(), Chalk.on("TypenName").blue().bold());
	System.out.println("\nArgumente sowie Rueckgabetyp einer Funktion sind optional, wenn du sie allerdings benutzt musst du sie auch angeben.");
	System.out.println("Zuse ist eine typensichere Programmiersprache, d.h. im Gegensatz zu einer dynamischen Programmiersprache muss immer der DatenTyp einer Variable angegeben werden!");
	System.out.println("Das macht Programmiersprachen 1. schneller, weil nicht zur Laufzeit deines Programmes der Typ geprueft werden muss Und 2. fuehrt es zu weniger Fehler beim Programmieren!");
	System.exit(1);
    }
}
