package util;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TypeInfo {

    public static final String NUMBERTYPE = "Zahl";
    public static final String BOOLEANTYPE = "Bool";
    public static final String STRINGTYPE = "Text";

    private String typeString;
    private Token location = null;

    /**
     * Describes if the type is nested in an array. Example: a: foo = ...; here a is of type foo and
     * not contained in an array, therefore the arraylevel would be 0. Example: a: [foo] = ...; here
     * a is of type array of foo and the arraylevel 1. a: [[foo]] = ...; a is of type of array of
     * array of foo and the arraylevel would be 2;
     *
     * <p>a.
     */
    private int arrayLevel = 0;

    public TypeInfo(String typeString) {
        this.typeString = typeString;
    }

    public TypeInfo(String typeString, Token location) {
        this.typeString = typeString;
        this.location = location;
    }

    public static boolean isAllowed(TypeInfo typeInfo, Token operator) {
        // // Numeric operations
        // if (typeInfo.type.equals(NUMBERTYPE) && operator.getType() == TokenType.PLUS) return
        // true;
        // if (typeInfo.type.equals(NUMBERTYPE) && operator.getType() == TokenType.MINUS) return
        // true;
        // if (typeInfo.type.equals(NUMBERTYPE) && operator.getType() == TokenType.MULTIPLY) return
        // true;
        // if (typeInfo.type.equals(NUMBERTYPE) && operator.getType() == TokenType.DIVIDE) return
        // true;
        // if (typeInfo.type.equals(NUMBERTYPE) && operator.getType() == TokenType.MODULO) return
        // true;

        // // Boolean operations
        // if (typeInfo.type.equals(BOOLEANTYPE) && operator.getType() == TokenType.AND) return
        // true;
        // if (typeInfo.type.equals(BOOLEANTYPE) && operator.getType() == TokenType.OR) return true;
        // if (typeInfo.type.equals(BOOLEANTYPE) && operator.getType() == TokenType.NOT) return
        // true;

        // if (typeInfo.type.equals(STRINGTYPE) && operator.getType() == TokenType.PLUS) return
        // true;
        // return false;
        return false;
    }
}
