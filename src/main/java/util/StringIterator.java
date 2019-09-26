package konrad.util;

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
}
