package konrad;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class SourceFile {

    public String filename;

    public ArrayList<String> sourceBuffer;
    
    public Iterator<String> lineIterator;
    public Iterator<Character> rowIterator;
    
    //Note(Simon): maybe we can abstract these away into something like a filePosition
	public int currentLine = 0;
    public int cursor = 0;
    
    public SourceFile(String filename) {
	this.filename = filename;
	this.sourceBuffer = readFile(filename);
	this.lineIterator = this.sourceBuffer.iterator();
	var charArr = this.lineIterator.next().toCharArray();
	List charList = Arrays.asList(charArr);
	this.rowIterator = charList.iterator();
    }

    public static ArrayList<String> readFile(String filename) {
	var buffer = new ArrayList<String>();
	try (var br = new BufferedReader(new FileReader(filename))) {
	    // NOTE(Simon): collecting the stream would probably be faster
	    String line;
	    while ((line = br.readLine()) != null) {
		// NOTE(Simon): maybe add char to empty line
		buffer.add(line);
	    }
	    br.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.printf("failed to read file %s %n", filename); //TODO(Simon): better error msg would be nice 
	}
	return buffer;
    }

    public boolean hasNext() {
	return rowIterator.hasNext() || lineIterator.hasNext();
    }

    public char next() {
	if (!rowIterator.hasNext()) {
	    if (!lineIterator.hasNext()) {
		System.out.println("failed");
	    }
	}
	return (Character) rowIterator.next();
    }

    public void updateRowIterator() {
	var charArr = this.lineIterator.next().toCharArray();
	List charList = Arrays.asList(charArr);
	this.rowIterator = charList.iterator();
    }

    public ArrayList<String> getSourceBuffer() {
	return this.sourceBuffer;
    }

    public void printDebug() {
	for (String line : this.sourceBuffer) {
	    System.out.println(line);
	}
    }
}
