package core;

import java.util.*;
import lombok.*;
import util.*;

public class Linter implements Stmt.Visitor<Void> {

    public void lint(List<Stmt> AST) {
        AST.stream().forEach(node -> node.accept(this));
    }

    public Void visitModuleStmt(Stmt.Module ASTNode) {
        return null;
    }

    @Override
    public Void visitImportStmt(Stmt.Import ASTNode) {
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break ASTNode) {
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While ASTNode) {
        return null;
    }

    @Override
    public Void visitVarDefStmt(Stmt.VarDef ASTNode) {
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return ASTNode) {
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print ASTNode) {
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If ASTNode) {
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.FunctionDecl ASTNode) {
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression ASTNode) {
        return null;
    }

    @Override
    public Void visitStructDeclStmt(Stmt.StructDecl ASTNode) {
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block ASTNode) {
        return null;
    }

    @Override
    public Void visitImplBlockStmt(Stmt.ImplBlock ASTNode) {
        return null;
    }
}
