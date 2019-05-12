package cn.cyx666.Mxstar.Scope.Scanner;import cn.cyx666.Mxstar.AST.*;

import cn.cyx666.Mxstar.AST.DeclarationNode.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;
import cn.cyx666.Mxstar.AST.ProgramNode.*;
import cn.cyx666.Mxstar.AST.StatementNode.*;
import cn.cyx666.Mxstar.Type.*;
import cn.cyx666.Mxstar.Error.*;

abstract public class ScopeScanner implements ASTVisitor {

    public void checkVariableInitialization(VariableDeclarationNode node) {
        if (node.getInitialization() != null) {
            boolean bool;
            node.getInitialization().accept(this);
            if (node.getType().getType() instanceof VoidType || node.getInitialization().getType() instanceof VoidType) bool = true;
            else if (node.getInitialization().getType() instanceof NullType) bool = !(node.getType().getType() instanceof ClassType || node.getType().getType() instanceof  ArrayType);
            else bool = !node.getType().getType().equals(node.getInitialization().getType());
            if (bool) {
                throw new SemanticError(node.position(), "Invalid variable initialization, expected \"" + node.getType().getType().toString() + "\" but got \"" + node.getInitialization().getType().toString() + "\"");
            }
        }
    }

    @Override
    public void visit(ProgramNode node) {}    

    @Override
    public void visit(ForStatementNode node) {}

    @Override
    public void visit(NewExpressionNode node) {}

    @Override
    public void visit(BlockStatementNode node) {}

    @Override
    public void visit(BreakStatementNode node) {}

    @Override
    public void visit(NullExpressionNode node) {}

    @Override
    public void visit(ThisExpressionNode node) {}

    @Override
    public void visit(TypeExpressionNode node) {}

    @Override
    public void visit(WhileStatementNode node) {}

    @Override
    public void visit(ReturnStatementNode node) {}

    @Override
    public void visit(AssignExpressionNode node) {}

    @Override
    public void visit(BinaryExpressionNode node) {}

    @Override
    public void visit(ClassDeclarationNode node) {}

    @Override
    public void visit(PrefixExpressionNode node) {}

    @Override
    public void visit(SuffixExpressionNode node) {}

    @Override
    public void visit(ContinueStatementNode node) {}

    @Override
    public void visit(ConditionStatementNode node) {}

    @Override
    public void visit(ExpressionStatementNode node) {}

    @Override
    public void visit(FunctionDeclarationNode node) {}

    @Override
    public void visit(SubscriptExpressionNode node) {}

    @Override
    public void visit(VariableDeclarationNode node) {}

    @Override
    public void visit(IdentifierExpressionNode node) {}

    @Override
    public void visit(IntConstantExpressionNode node) {}

    @Override
    public void visit(BoolConstantExpressionNode node) {}

    @Override
    public void visit(FunctionCallExpressionNode node) {}

    @Override
    public void visit(MemberAccessExpressionNode node) {}

    @Override
    public void visit(VariableListExpressionNode node) {}

    @Override
    public void visit(StringConstantExpressionNode node) {}
}
