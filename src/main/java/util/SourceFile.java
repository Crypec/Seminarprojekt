package kuzuto.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class SourceFile {

  private String filename;
  private ArrayList<String> sourceBuffer;
  private int currentLine = 0;

  public SourceFile(String filename) {
    this.filename = filename;
    this.sourceBuffer = readFile(filename);
  }

  private static ArrayList<String> readFile(String filename) {
    var buffer = new ArrayList<String>();
    try (var br = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = br.readLine()) != null) {
        // NOTE(Simon): maybe add char to empty line
        buffer.add(line);
      }
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.printf(
          "failed to read file %s %n", filename); // TODO(Simon): better error msg would be nice
    }
    return buffer;
  }

  public boolean hasNext() {
    return sourceBuffer.size() > currentLine;
  }

    public String next() {
	return sourceBuffer.get(currentLine++);
    }

    public String getFilename() {
	return this.filename;
    }

    public int getLine() {
	return this.currentLine;
    }
}
