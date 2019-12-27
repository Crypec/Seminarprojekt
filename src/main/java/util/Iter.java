package util;

import com.google.gson.*;

import java.util.*;
import java.util.stream.*;

//TODO(Simon): Extend Iterator to make suitable for use in the parser 
public class Iter<T> implements Iterator {

    private T[] buffer;
    private int cursor = 0;

    public Iter(T[] buffer) {
	this.buffer = buffer;
    }
    public Iter() {}

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
	if (!this.hasNext()) return null;
	return buffer[cursor];
    }

    public T peekNext() {
	if (cursor +1 > buffer.length) {
	    return null;
	}
	return buffer[cursor +1];
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

    public void setBuffer(T[] buffer) {
	this.buffer = buffer;
    }

    public int getCursor() {
	return this.cursor;
    }

    @Override
    public String toString() {
	return new GsonBuilder()
	    .setPrettyPrinting()
	    .serializeNulls()
	    .create()
	    .toJson(this);
    }
}
