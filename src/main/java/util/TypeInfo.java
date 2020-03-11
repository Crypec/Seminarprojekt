package util;

import java.util.*;
import java.util.stream.*;
import lombok.*;
import lombok.experimental.*;
import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
public abstract class TypeInfo implements Serializable {

	public static final String NUMBERTYPE = "Zahl";
	public static final String BOOLEANTYPE = "Bool";
	public static final String STRINGTYPE = "Text";
	public static final String VOIDTYPE = "()";

	@EqualsAndHashCode.Exclude private Token location;

	@EqualsAndHashCode(callSuper=false)
	@Getter
	@SuperBuilder
	public static class Array extends TypeInfo {
		private TypeInfo elementType;

		@Override
		public String getBaseTypeString() {
			return elementType.getBaseTypeString();
		}

		@Override
		public String toString() {
			return "[" + elementType.toString() + "]";
		}
	}

	@Getter
	@EqualsAndHashCode(callSuper=false)
	@SuperBuilder
	public static class Primitive extends TypeInfo {
		private String typeString;

		@Override
		public String getBaseTypeString() {
			return this.typeString;
		}

		@Override 
		public String toString() {
			return this.typeString;
		}
	}

	@Getter
	@EqualsAndHashCode(callSuper=false)
	@SuperBuilder
	public static class Tuple extends TypeInfo {
		private ArrayList<TypeInfo> elementTypes;
		@Override
		public String getBaseTypeString() {
			throw new UnsupportedOperationException(
													"A tuple consists of more than 1 type and does therefor not have a single basetypestring!");
		}
		@Override
		public String toString() {
			return "(" + elementTypes.stream().map(e -> e.toString()).collect(Collectors.joining(",")) + ")";
		}
	} 

	public static TypeInfo.Primitive voidType(Token t) {
		return TypeInfo.Primitive.builder().typeString(TypeInfo.VOIDTYPE).location(t).build();
	}

	public abstract String getBaseTypeString();

	public static boolean isAllowed(TypeInfo type, Token operator) {

		if (!(type instanceof TypeInfo.Primitive)) return false;
		TypeInfo.Primitive cmp = (TypeInfo.Primitive) type;
		return switch (cmp.typeString) {
		case NUMBERTYPE:
			yield switch (operator.getType()) {
			case PLUS, MINUS, MULTIPLY, DIVIDE, EQUALEQUAL, NOTEQUAL, LESS, LESSEQUAL, GREATER, GREATEREQUAL:
				yield true;
			default:
				yield false;
			};
		case BOOLEANTYPE:
			yield switch (operator.getType()) {
			case AND, OR, NOT, EQUALEQUAL, NOTEQUAL:
				yield true;
			default:
				yield false;
			};
		case STRINGTYPE:
			yield switch (operator.getType()) {
			case EQUALEQUAL, NOTEQUAL:
				yield true;
			case PLUS: {
				Report.builder().wasFatal(true).errType("TypenFehler").errMsg(
						"In dieser Sprache duerfen Zeichenfolgen nicht mit dem + Operator anneinanderehaengt werden, du solltest dafuer die 'format' Funktion nutzen!")
					.example("""
							 foo: Text = format("Hello {}", "World");
							 """).example("""
							 gruss := format("Hallo {}, wie geht es dir heute?", name);
							 """).example("""
								gedreht := format("{0} {1}", "Welt :D" , "Hallo");
							 """).build().print();
				yield false;
			}
			default:
				yield false; // use format function to concat strings
			};
		default:
			yield false;
		};
	}
}
