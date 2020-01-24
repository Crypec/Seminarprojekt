package core;

import java.text.*;
import java.util.*;
import lombok.*;
import util.*;

public class Typer implements Expr.Visitor<Void> {

    public void infer(Expr ASTNode) {
        ASTNode.accept(this);
    }

    @Override
    public Void visitArrayAccessExpr(Expr.ArrayAccess expr) {
        return null;
    }

    @Override
    public Void visitStructLiteralExpr(Expr.StructLiteral expr) {
        return null;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        return null;
    }

    @Override
    public Void visitInputExpr(Expr.Input expr) {
        expr.setType(new TypeInfo(TypeInfo.STRINGTYPE));
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        expr.getRight().accept(this);
        return null;
    }

    @Override
    public Void visitSelfExpr(Expr.Self expr) {
        return null;
    }

    @Override
    public Void visitSetExpr(Expr.Set expr) {
        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        if (expr.getValue() instanceof Double) {
            expr.setType(new TypeInfo(TypeInfo.NUMBERTYPE));
        } else if (expr.getValue() instanceof String) {
            expr.setType(new TypeInfo(TypeInfo.STRINGTYPE));
        } else if (expr.getValue() instanceof Boolean) {
            expr.setType(new TypeInfo(TypeInfo.BOOLEANTYPE));
        } else {
            System.out.println(
                    "Internal Error you should never see this :: type error for literal");
        }
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        expr.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {

        expr.getLeft().accept(this);
        expr.getRight().accept(this);

        var err =
                Report.builder()
                        .wasFatal(true)
                        .errType("Typenfehler")
                        .errMsg("Kann folgende Operation nicht auf folgende Typen ausfuehren!")
                        .token(null)
                        .url(null)
                        .build();

        boolean failed = false;

        // if (right.getType().getTypeTag() != left.getTypeInfo().getTypeTag()) {
        //     err.setToken(expr.getOperator());
        //     failed = true;
        // } else if (!TypeInfoTag.isAllowed(right.getTypeInfo(), expr.getOperator())) {
        //     System.out.println(err);
        //     failed = true;
        // }

        // if (failed) return null;
        // expr.setTypeInfo(right.getTypeInfo());
        return null;
    }
}
