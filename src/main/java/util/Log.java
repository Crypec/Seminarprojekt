package konrad.util;

import konrad.util.*;
import com.github.tomaslanger.chalk.*;

public class Log {

    private static boolean hadError = false;

    public static void reportErr(String errType, String msg, Token token) {
	hadError = true;
    }

    public static void reportWarn() {
	
    }
}
