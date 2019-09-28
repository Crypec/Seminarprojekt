package konrad.util;

// @PERF(Simon): this is just a workaround until I have  a better idea to do error handling
// it is not efficient to save all the metadata like filenames in every token of a sourcefile
public class MetaData {
    public String filename;
    public int line;
    public int colum;

    public MetaData(String filename, int line, int colum) {
	this.filename = filename;
	this.line = line;
	this.colum = colum;
    }

}
