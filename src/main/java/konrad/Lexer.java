package konrad;

import java.util.ArrayList;
import konrad.util;

public class Lexer {

    private SourceFile source;
    private ArrayList<Token> tokenStream;

    public void lex(SourceFile source) {
	while (this.source.hasNext()) {
	    System.out.println(source.next());
	}
    }
}
