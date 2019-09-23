package konrad.util.common;

import konrad.util.common.*;
import konrad.util.*;

public class Log {

    public static void reportError(String errorType, String errorMsg, MetaData meta) {
	System.out.println(errorMsg);
    }


    public void reportWarning(String msg) {
	System.out.println(msg);
    }
}
