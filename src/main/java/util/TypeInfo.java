package util;

import com.google.gson.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TypeInfo {

  public static final String NUMBERTYPE = "Zahl";
  public static final String BOOLEANTYPE = "Bool";
  public static final String STRINGTYPE = "Text";

  private String typeString;
  private Token location;
  /**
     If a subtreee contains a type marked as dirty, the whole expression tree is
     invalid.

     Example:
     a := 2 + wahr; // this expression is not valid because we cant add a number
     with an boolean value in this language. b := a + 2; // because the whole
     expression tree of a is dirty, we cant proceed to validate b.

     The isDirty value has therefore to be  propagated through the whole
     expression tree. And we cant just mark one type interaction as dirty.
  */
  private boolean isDirty;

  /**
   * Describes if the type is nested in an array. Example: a: foo = ...; here a
   * is of type foo and not contained in an array, therefore the arraylevel
   * would be 0. Example: a: [foo] = ...; here a is of type array of foo and the
   * arraylevel 1. a: [[foo]] = ...; a is of type of array of array of foo and
   * the arraylevel would be 2;
   */
  private int arrayLevel;

  public static boolean isAllowed(TypeInfo typeInfo, Token operator) {
    if (typeInfo.arrayLevel != 0) {
      return false;
    }
    return switch (typeInfo.typeString) {
    case NUMBERTYPE:
      yield switch (operator.getType()) {
      case PLUS, MINUS, MULTIPLY, DIVIDE:
        yield true;
      default:
        yield false;
      };
    case BOOLEANTYPE:
      yield switch (operator.getType()) {
      case AND, OR, NOT:
        yield true;
      default:
        yield false;
      };
    case STRINGTYPE:
      yield switch (operator.getType()) {
      case PLUS: {
                    Report.builder()
						.wasFatal(true)
						.errType("TypenFehler")
						.errMsg("In dieser Sprache duerfen Zeichenfolgen nicht mit dem + Operator anneinanderehaengt werden, du solltest dafuer die 'format' Funktion nutzen!")
						.example("""
							 foo: Text = format("Hello {}", "World");
							 """)
						.example("""
							 gruss := format("Hallo {}, wie geht es dir heute?", name);
							 """)
						.example("""
							 gedreht := format("{0} {1}", "Welt :D" , "Hallo");
							 """)
						.build()
						.print();
				yield false;
      }
      default:
        yield false; // use format function to concat strings
      };
    default:
      yield false;
    };
  }

  public void setIsDirty(boolean isDirty) { this.isDirty = isDirty; }

  public boolean getIsDirty() { return this.isDirty; }

  @Override
  public String toString() {
		return "[".repeat(arrayLevel) + typeString + "]".repeat(arrayLevel);
  }
}
