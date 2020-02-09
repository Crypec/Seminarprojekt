package util;

import com.google.gson.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Report {

    // TODO(Simon): rename to somehting more generic, we use the same excpetion in the typechecking
    // stage as well
    public class Error extends RuntimeException {}

    // if we encounter a fatal error while parsing we set this field to not execute corrupted code
    public static boolean hadErr = false;

    private boolean wasFatal = false;

    private String errType;
    private String errMsg;

    private List<String> examples = new ArrayList();

    private Token token;
    private String url;

    public static ReportBuilder builder() {
        return new ReportBuilder();
    }

    @NoArgsConstructor
    public static class ReportBuilder {

        private boolean wasFatal = false;

        private String errType;
        private String errMsg;

        public List<String> examples = new ArrayList();

        private Token token;
        private String url;

        public ReportBuilder example(String example) {
            this.examples.add(example);
            return this;
        }

        public ReportBuilder errType(String errType) {
            this.errType = errType;
            return this;
        }

        public ReportBuilder errMsg(String errMsg) {
            this.errMsg = errMsg;
            return this;
        }

        public ReportBuilder wasFatal(boolean wasFatal) {
            this.wasFatal = wasFatal;
            return this;
        }

        public ReportBuilder token(Token token) {
            this.token = token;
            return this;
        }

        public ReportBuilder url(String url) {
            this.url = url;
            return this;
        }

        public Report build() {
            return new Report(wasFatal, errType, errMsg, examples, token, url);
        }
    }

    public void sync() {
        if (wasFatal) {
            Report.hadErr = true;
            throw new Report.Error();
        }
    }

    public Report print() {
        System.err.println(this);
		return this;
	}

	@Override
	public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
