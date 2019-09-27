package konrad.util;

public class Argument {

    private String variablenName;
    private TokenType typ;

    public Argument(String name, TokenType typ) {
	this.variablenName = name;
	this.typ = typ;
    }

}
