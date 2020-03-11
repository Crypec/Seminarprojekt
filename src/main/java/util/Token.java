package util;

import com.google.gson.*;
import java.io.Serializable;
import java.util.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class Token implements Serializable {

	private TokenType type;
	private String lexeme;
	private Object literal;

	private String fileName;
	private int line;

	private int start;
	private int end;

	public Token(TokenType type, String lexeme) {
		this.type = type;
		this.lexeme = lexeme;
	}

	@Override
	public String toString() {
		return new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create()
			.toJson(this);
	}
}
