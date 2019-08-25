package konrad;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class SourceFile {

    public ArrayList<String> lineBuffer;

    public String filename;

    public int row = 0;
    // WARN(Simon): col has starting value of -1 because we want to get the first,
    // not the 2. element in the first iteration step
    public int col = -1;

    public SourceFile(String filename) {
	this.filename = filename;
	this.lineBuffer = readFile(filename);
	// System.out.println(lineBuffer.get(0).charAt(57));
    }

    public SourceFile() {
	this.filename = null;
	this.lineBuffer = null;
    }

    public static ArrayList<String> readFile(String filename) {
	var buffer = new ArrayList<String>();
	try (var br = new BufferedReader(new FileReader(filename))) {
	    // NOTE(Simon): collecting the stream would probably be faster
	    String line;
	    while ((line = br.readLine()) != null) {
		buffer.add(line);
	    }
	    br.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.printf("failed to read file %s %n", filename);
	}
	return buffer;
    }

    public boolean hasNext() {
	if (col < lineBuffer.get(row).length() || row < lineBuffer.size()) {
	    return true;
	} else {
	    return false;
	}
    }

    public char peek() { return 't'; }

    public char next() {
	if (col < lineBuffer.get(row).length() -1) {
	    this.col++;
	} else {
	    this.col = 0;
	    this.row++;
	}
	if (lineBuffer.get(row).length() == 0) {
	    return next();
	} else {
	    return lineBuffer.get(row).charAt(col);   
	}
    }
}
