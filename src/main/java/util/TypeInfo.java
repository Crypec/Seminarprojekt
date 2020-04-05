package util;

import java.util.*;
import static util.Utility.memClone;
import java.util.stream.*;
import lombok.*;
import lombok.experimental.*;
import java.io.Serializable;
import org.javatuples.*;

@Getter
@Setter
@SuperBuilder
public abstract class TypeInfo implements Serializable {

	public static final String NUMBERTYPE = "Zahl";
	public static final String BOOLEANTYPE = "Bool";
	public static final String STRINGTYPE = "Text";
	public static final String VOIDTYPE = "()";

	@EqualsAndHashCode.Exclude
	private Token location;

	@EqualsAndHashCode(callSuper = false)
	@Getter
	@SuperBuilder
	public static class Array extends TypeInfo {
		private TypeInfo elementType;

		@Override
		public String getBaseTypeString() {
			return elementType.getBaseTypeString();
		}

		@Override
		public void setBaseTypeString(String baseTypeString) {
			elementType.setBaseTypeString(baseTypeString);
		}

		@Override
		public String toString() {
			return "[" + elementType.toString() + "]";
		}
	}

	@Getter
	@EqualsAndHashCode(callSuper = false)
	@SuperBuilder
	public static class Primitive extends TypeInfo {
		private String typeString;

		@Override
		public String getBaseTypeString() {
			return this.typeString;
		}

		@Override
		public void setBaseTypeString(String baseTypeString) {
			this.typeString = baseTypeString;
		}

		@Override
		public String toString() {
			return this.typeString;
		}
	}

	@Getter
	@EqualsAndHashCode(callSuper = false)
	@SuperBuilder
	public static class Tuple extends TypeInfo {
		private ArrayList<TypeInfo> elementTypes;

		@Override
		public String getBaseTypeString() {
			throw new UnsupportedOperationException(
					"A tuple consists of more than 1 type and does therefor not have a single basetypestring!");
		}

		@Override
		public void setBaseTypeString(String baseTypeString) {
			throw new UnsupportedOperationException("A tuple consists of more than 1 type and does therefor not have a baseTypeString which could be set");
		}

		@Override
		public String toString() {
			return "(" + elementTypes.stream().map(e -> e.toString()).collect(Collectors.joining(",")) + ")";
		}

		public int size() {
			return elementTypes.size();
		}
	}

	@Getter
	@EqualsAndHashCode(callSuper = false)
	@SuperBuilder
	public static class Struct extends TypeInfo {

		private Stmt.StructDecl internalTypeDecl;

			@Override
			public String getBaseTypeString() {
				return internalTypeDecl.getStringName();
			}

		@Override
		public void setBaseTypeString(String baseTypeString) {
			throw new UnsupportedOperationException(
					"A tuple consists of more than 1 type and does therefor not have a baseTypeString which could be set");
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
	public abstract void setBaseTypeString(String baseTypeString);

	public static boolean isAllowed(TypeInfo type, Token operator) {

		boolean isPrimitive = type instanceof TypeInfo.Primitive;
		boolean isTuple = type instanceof TypeInfo.Tuple;

		// if (isTuple) {
		// val tuple = (TypeInfo.Tuple) type;
		// if (tuple.size() > 1) {
		// return false;
		// }
		// } else if (!isPrimitive) {

		// }
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

		public static Optional<TypeInfo> reduce(TypeInfo lhs, TypeInfo rhs, Token op) {
			// TODO(Simon): here we could manipulate the start and end of the expr "location"... to better produce error messages
			if (lhs.equals(rhs)) {
				if (isAllowed(lhs, op)) {
					if (op.getType().isComparisonOperator()){
						return Optional.of(TypeInfo.Primitive.builder()
									.typeString(TypeInfo.BOOLEANTYPE)
									.location(op)
									.build());
					}
					return Optional.of((TypeInfo)memClone(lhs));
				}
				return Optional.empty();
			}
			return Optional.empty();
	}
}
