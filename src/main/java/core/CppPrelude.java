package core;

import lombok.*;

public class CppPrelude {

	@Getter @Setter @AllArgsConstructor
	public static class Function {
		private String name;
		private String body;
	}

	public static final String importPrelude = """
		#include <fmt/format.h>
		#include <iostream>
		""";


	public static final String input = "__internal_get_input";
}
