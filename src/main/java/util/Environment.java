package util;

import java.util.*;

public class Environment {

    private final Map<String, Object> values = new HashMap();

    Object get(Token name) {
	if (values.containsKey(name.getLexeme())) {
	    return values.get(name.getLexeme());
	}
	return null;
	// TODO (Simon): should we report a runtime error if the variable is not found, because we should theoretically do detect this during the typechecking phase
    }
}
