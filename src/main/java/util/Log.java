package konrad.util;

import konrad.util.*;
import com.github.tomaslanger.chalk.*;

public class Log {

    public static void reportWarning(String errorType, String msg) {
	System.out.printf("%s :: %s:  %n%s%n", Chalk.on("WARNUNG").yellow().bold().underline(), Chalk.on(errorType).yellow().bold(), msg);
    }

    public static void printErrorHeader(String errorType) {
	System.out.printf("%s :: %s!", Chalk.on("ERROR").red().bold().underline(), errorType);

    }
    public static void invalidTokenError(MetaData meta) {

	printErrorHeader("Invalider Token -> [ExprParser]");

	System.exit(1);
    }

}
