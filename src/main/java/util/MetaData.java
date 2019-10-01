package konrad.util;

// PERF(Simon): this is just a workaround until I have  a better idea to do error handling
// it is not efficient to save all the metadata like filenames in every token of a sourcefile
public class MetaData {
    public String filename;
    public int line;
    public int colum;

    // TODO (Simon): Save start and end positon of token to get better error reporting 
    // TODO (Simon): maybe int startPos, int endPos???

    public MetaData(String filename, int line, int colum) {
	this.filename = filename;
	this.line = line;
	this.colum = colum;
    }
}
