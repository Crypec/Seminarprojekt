package util;

import java.util.*;

public class Environment {

    private final Map<String, Object> values = new HashMap();

    Object get(Token name) {
	if (values.containsKey(name.getLexeme())) {
	    return values.get(name.getLexeme());
	}
	return null;
	// TODO (Simon): throw runtime error if variable is not found in environment
    }
    
}
