package konrad;

import java.util.*;
import konrad.util.*;

public class Parser {

    private int position = 0;
    
    public static ASTNode parseKopfVonFunktion(ArrayList<Token> tokenList) {

	var kopf = new FunktionsKopf();

	if (tokenList.get(position).getType() == TokenType.FUNCTION) {
	    
	}
    }

    
}
