package kuzuto;

import util.*;

public class Interp implements Expr.Visitor<Object> {

    @Override
    public static Object visitLiteralExpr(Expr.Literal expr) {
	return expr.value;
    }

    public static evalute(Expr expr) {
	return expr.accept();
    }

}
