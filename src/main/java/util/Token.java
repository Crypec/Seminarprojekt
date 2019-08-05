package konrad.util;

public class Token {

    //TODO(Simon): the filename String is redundant and only used for
    //TODO(Simon): error messages. We should optimize it away so that
    //TODO(Simon): all tokens of a common translation unit share a filelocation
    //NOTE(Simon): Maybe we should move the tokenStream output from the Lexer to the SourceFile
    public String filename;
    public TokenType type;
    public Object value = null;

    public Token(String filename, TokenType type) {
	this.filename = filename;
	this.type = type;
    }

    public Token(String filename, TokenType type, Object value) {
	this.filename = filename;
	this.type = type;
	this.value = value;
    }
}
