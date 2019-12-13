package util;

import java.util.*;
import java.lang.*;

import com.google.gson.*;

public class Report {


    // TODO(Simon): rename to somehting more generic, we use the same excpetion in the typechecking stage as well
    public class ParseError extends RuntimeException{}

    // if we encounter a fatal error while parsing we set this field to not execute corrupted code
    public static boolean hadErr = false;

    private boolean isFatal = false; 

    private String errType;
    private String errMsg;
    private List<String> examples;
    private Token token;

    // url to resources where the user can learn more about his error
    private String url;

    public Report(String errType, String errMsg, List<String> examples,
		  Token token, String url, boolean isFatal) {
	
	this.errType = errType;
	this.errMsg = errMsg;
	this.examples = examples;
	this.token = token;
	this.url = url;
	this.isFatal = isFatal;
    }

    public static class Builder {

	private boolean isFatal = false;
	
	private String errType;
	private String errMsg;
	private List<String> examples;
	private Token token;
	private String url;

	public Builder() { this.examples = new ArrayList<String>(); }

	public Builder errWasFatal() {
	    this.isFatal = true;
	    Report.hadErr = true;
	    return this;
	}

	public Builder setErrorType(String errType) {
	    this.errType = errType;
	    return this;
	}

	public Builder withErrorMsg(String errMsg) {
	    this.errMsg = errMsg;
	    return this;
	}

	public Builder addExample(String example) {
	    this.examples.add(example);
	    return this;
	}

	public Builder atToken(Token token) {
	    this.token = token;
	    return this;
	}

	public Builder url(String url) {
	    this.url = url;
	    return this;
	}

	public Report create() {
	    return new Report(this.errType, this.errMsg, this.examples, this.token,
			      this.url, isFatal);
	}
    }

    public void setToken(Token t) {
	this.token = t;
    }
    
    
    // if we encounter an error during the parsing stage we can use the following exception to restore state and continue parsing till the end
    // this means we can try to detect all errors and not just a single error every time the user tries to compile a programm
    public void sync() throws ParseError {
	if (this.isFatal) throw new ParseError();
    }

    // TODO(Simon): Implement nice pretty prining of error/warning messages
    @Override
    public String toString() {
	return new GsonBuilder()
	    .setPrettyPrinting()
	    .serializeNulls()
	    .create()
	    .toJson(this);
    }
}
