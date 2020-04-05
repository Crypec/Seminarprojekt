package util;

import java.io.*;

public class Utility {
	public static Object memClone(Object object) {
		try {
			var outputStream = new ByteArrayOutputStream();
			var outputStrm = new ObjectOutputStream(outputStream);
			outputStrm.writeObject(object);
			var inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			var objInputStream = new ObjectInputStream(inputStream);
			return objInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
