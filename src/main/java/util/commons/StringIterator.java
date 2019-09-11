package konrad.util.common;

import java.util.Iterator;


public class StringIterator implements Iterator {

    private String buffer;
    private int cursor = 0;

    public StringIterator(String buffer) {
	this.buffer = buffer;
    }

    public boolean hasNext() {
	return buffer.length() > cursor;
    }

    public Character previous() {
	return buffer.charAt(cursor -1);
    }
    public Character current() {
	return buffer.charAt(cursor);
    }

    public Character next() {
	return buffer.charAt(cursor++);
    }


    public void remove() {
	throw new UnsupportedOperationException();
    }
}
