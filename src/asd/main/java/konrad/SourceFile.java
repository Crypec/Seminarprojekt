package konrad;

public class SourceFile {

    public List<String> lines;

    public SourceFile(String filename) {

	this.lines = readFile(filename);
	
    }

    public static ArrayList<String> readFile(String filename) {
	FileReader fileReader;
	BufferReader bufferReader;
	try {
	    fileReader = new FileReader(filename);
	    bufferReader = new BufferReader(fileReader);
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.printf("failed to read file %s %n", filename);

	} finally {
	    bufferReader.close();
	}
    }
}
