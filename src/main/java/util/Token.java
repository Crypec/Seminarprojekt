package util;

import com.google.gson.*;

import java.util.*;
import core.Lexer;

public class Token {

    // also these fields should't be public :D
    private TokenType type;
    private String lexeme;
    private Object literal;
    private MetaData meta;
    
    public Token(String lexeme, MetaData meta, TokenType type, Object literal) {
	this.lexeme = lexeme;
	this.meta = meta;
	this.type = type;
	this.literal = literal;
    }

    public Token(TokenType type) {
	this.type = type;
	this.lexeme = null;
	this.literal = null;
	this.meta = new MetaData("");
    }

    public static class Builder {
	
	private String lexeme;
	private TokenType type;
	private Object literal = null;
	private MetaData meta = new MetaData();

	public Builder filename(String filename) {
	    this.meta.setFilename(filename);
	    return this;
	}

	public Builder line(int line) {
	    this.meta.setLine(line);
	    return this;
	}

	public Builder position(int start, int end) {
	    this.meta.setStartPos(start);
	    this.meta.setEndPos(end);
	    return this;
	}
	
	public Builder lexeme(String lexeme) {
	    this.lexeme = lexeme;
	    return this;
	}


	public Builder withType(TokenType type) {
	    this.type = type;
	    return this;
	}

	public Builder withLiteral(Object literal) {
		this.literal = literal;
		return this;
	}

	public Token build() {
	    return new Token(this.lexeme, this.meta, this.type, this.literal);
	}
    }
    
    public void setType(TokenType type) {
	this.type = type;
    }

    public void setLexeme(String lexeme) {
	this.lexeme = lexeme;
    }
    
    public void setLiteral(Object literal) {
	this.literal = literal; 
    }

    public void setMeta(MetaData meta) {
	this.meta = meta;
    }
    
    public TokenType getType() { return this.type; }
    
    public Object getLiteral() { return this.literal; }

    public String getLexeme() { return this.lexeme; }

    public MetaData getMeta() { return this.meta; }

    @Override
    public String toString() {
	return new GsonBuilder()
	    .setPrettyPrinting()
	    .serializeNulls()
	    .create()
	    .toJson(this);
    }
}
