package util;

import java.util.*;
import java.util.stream.*;

//TODO(Simon): Extend Iterator to make suitable for use in the parser 
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
	cursor -= 1;
    }

    public void remove() {
	throw new UnsupportedOperationException();
    }

    public Stream<T> stream() {
	return Stream.of(buffer);
    }

    public T[] getBuffer() {
	return this.buffer;
    }

    public int getCursor() {
	return this.cursor;
    }
}
