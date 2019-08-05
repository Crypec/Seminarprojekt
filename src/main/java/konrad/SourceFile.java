package konrad;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class SourceFile {

    public ArrayList<String> lineBuffer;

    public String filename;
    public int rowIndex = 0;
    public int colIndex = 0; 

    public SourceFile(String filename) {
	this.filename = filename;
	this.lineBuffer = readFile(filename);
    }

    public static ArrayList<String> readFile(String filename) {
	var buffer = new ArrayList<String>();
	try (var br = new BufferedReader(new FileReader(filename))){
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

    public String getPath() {
	return this.filename;
    }

    public ArrayList<String> getBuffer() {
	return this.lineBuffer;
    }

    public String getRow(int i) {
	return this.lineBuffer.get(i);
    }

    public int getRowIndex() {
	return this.rowIndex;
    }

    public int getColIndex() {
	return this.colIndex;
    }

    public boolean hasNext() {
	return this.lineBuffer.size() > this.rowIndex && this.lineBuffer.get(rowIndex).length() > this.colIndex;
    }
    
    public int rowLength() {
	return this.lineBuffer.get(this.colIndex).length();
    }


    public char next() {
	if (this.rowLength() == this.colIndex) {
	    this.colIndex = 0;
	    this.rowIndex += 1;
	}
	System.out.printf("%d %d", rowIndex, colIndex);
	return this.lineBuffer.get(this.rowIndex).charAt(this.colIndex);
    }
}
