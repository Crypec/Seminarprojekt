package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class SourceFile {

    private String filename;
    private int line;
    private Iter<String> lineIter;

    public SourceFile(String filename) {
	this.filename = filename;
	this.lineIter = new Iter<String>(readFile(filename).toArray(new String[0]));
    }

    private static ArrayList<String> readFile(String filename) {
	var buffer = new ArrayList<String>();
	try (var br = new BufferedReader(new FileReader(filename))) {
	    String line;
	    while ((line = br.readLine()) != null) {
		// NOTE(Simon): maybe add char to empty line
		buffer.add(line);
	    }
	    br.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.printf(
			      "failed to read file %s %n", filename); // TODO(Simon): better error msg would be nice
	}
	return buffer;
    }

    public Iter<String> getIter() {
	return this.lineIter;
    }
    

    public String getFilename() {
	return this.filename;
    }
}
