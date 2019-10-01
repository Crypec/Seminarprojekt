package konrad.util;

// TODO (Simon): we shouldn't save the filename in all tokens
public class MetaData {

    // HACK(Simon): Refactor to be private
    public String filename;
    public int lineNumber = 0;

    public int startPosition = 0;
    public int endPosition = 0;

    public MetaData(String filename, int start, int end, int lineNumber) {
	this.lineNumber = 0;
	this.filename = filename;
	this.startPosition = start;
	this.endPosition = end;
    }

    public MetaData() {
	this.filename = "[Error] filename not specified";

	this.lineNumber = 0;
	this.startPosition = 0;
	this.endPosition = 0;
    }

    public MetaData(String filename) {
	this.filename = filename;
    }
}
