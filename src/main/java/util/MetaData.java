package konrad.util;

// TODO (Simon): we shouldn't save the filename in all tokens
public class MetaData {

    // HACK(Simon): Refactor to be private
    private String filename;
    private int lineNumber = 0;

    private int startPosition = 0;
    private int endPosition = 0;

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

    public MetaData(String filename) { this.filename = filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public void setLine(int line) { this.lineNumber = line; }

    public void setStartPos(int start) { this.startPosition = start; }

    public void setEndPos(int end) { this.endPosition = end; }

    public String getFilename() { return this.filename; }

    public int getStartPos() { return this.startPosition; }

    public int getEndPos() { returrn this.endPosition; }

    public int getLine() { return this.lineNumber; }
}
