package konrad.util;

import java.util.Iterator;

public class Iter<T> implements Iterator {

    private T[] buffer;
    private int cursor = 0;

    public Iter(T[] buffer) {
	this.buffer = buffer;
    }

    public boolean hasNext() {
	return buffer.length > cursor;
    }

    public T previous() {
	return buffer[cursor -1];
    }

    public T next() {
	return buffer[cursor++];
    }

    // peeks looks at the item directly under the cursor
    // which means peeking one item into the future
    public T peek() {
	if (!this.hasNext()) {
	    return null;
	}
	return buffer[cursor];
    }

    public void setBackOnePosition() {
	cursor--;
    }

    public void remove() {
	throw new UnsupportedOperationException();
    }

    public ArrayList<Token> getUntil(T needle){

	var sub = new ArrayList<T>();
	var haystack = al.subList(this.cursor, this.buffer.length);

	for (T elem : haystack) {
	    if (elem == needle) {
		return sub;
	    }
	}
    }

}
