package util;

import com.google.gson.*;

import java.util.*;
import lombok.*;
import java.io.Serializable;
import core.Lexer;

@Getter @Setter @Builder @AllArgsConstructor @EqualsAndHashCode
public class Token implements Serializable {

    private TokenType type;
    private String lexeme;
    private Object literal;

    private String fileName;
    private int line;

    private int start;
    private int end;

    public Token(TokenType type) {
	this.type = type;
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
