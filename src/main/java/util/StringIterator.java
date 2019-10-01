package konrad.util;

import java.util.Iterator;

public class StringIterator implements Iterator {

    private String buffer;
    private int cursor = 0;

    private String filename;
    private int line; 

    public StringIterator(String buffer, SourceFile sf) {
	this.buffer = buffer;
	this.filename = sf.getFilename();
	this.line = sf.getLine();
    }

    public boolean hasNext() {
	return buffer.length() > cursor;
    }

    public Character previous() {
	return buffer.charAt(cursor - 1);
    }

    public Character next() {
	return buffer.charAt(cursor++);
    }

    // peeks looks at the item directly under the cursor
    // which means peeking one item into the future
    public Character peek() {
	return buffer.charAt(cursor);
    }

    public void setBackOnePosition() {
	cursor--;
    }

    

    public void remove() {
	throw new UnsupportedOperationException();
    }
    
    public String getFilename() {
	return this.filename;
    }
    public int getLine() {
	return this.lineNumber;
    }
}
