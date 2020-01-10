package util;

import util.*;
import com.github.tomaslanger.chalk.*;

public class Log {

    private static boolean hadError = false;

    public static void reportErr(String errType, String msg, Token token) {
	hadError = true;
	System.out.println(Chalk.on(errType).bold().red());
	System.out.println(msg);
	System.out.println(token.getMeta().getLine());
    }

    public static void reportWarn(String warnType, String msg, Token token) {

    }

    public static void exitOnError() {
	if (hadError) System.exit(1);
    }
}
