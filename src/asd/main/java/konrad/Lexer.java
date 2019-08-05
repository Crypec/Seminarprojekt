public class Lexer {

    private int index = 0;
    private  int line = 0; 
    private List<String> source;

    public char peek() {
	return this.source.get(line).charAt(index +1);
    }

    public char next() {
	this.index++;
	return this.source.get(Line).charAt(index);
    }

    public char current() {
	return this.source.get(line).charAt(index);
    }

    public Lexer(List<String> source) {
	this.source = source;
    }
    

    public void lex(ArrayList<String> lines) {
	for (String line : lines) {
	    for 
		}
    }

    
    
}
