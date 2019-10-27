package util;

import com.google.gson.*;
import java.util.*;

public class Report {

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
		  Token token, String url) {
	
	this.errType = errType;
	this.errMsg = errMsg;
	this.examples = examples;
	this.token = token;
	this.url = url;
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
			      this.url);
	}
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
