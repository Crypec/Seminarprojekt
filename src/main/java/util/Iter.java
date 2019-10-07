package konrad.util;

import java.util.Iterator;

//TODO(Simon): factor filename and lineNumber dependecy out!
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
	return buffer[cursor];
    }

    public void setBackOnePosition() {
	cursor--;
    }

    public void remove() {
	throw new UnsupportedOperationException();
    }
}
