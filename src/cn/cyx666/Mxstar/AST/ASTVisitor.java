package cn.cyx666.Mxstar.AST;

import cn.cyx666.Mxstar.AST.DeclarationNode.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;
import cn.cyx666.Mxstar.AST.StatementNode.*;
import cn.cyx666.Mxstar.AST.ProgramNode.ProgramNode;

public interface ASTVisitor {
    //ProgramNode
    void visit(ProgramNode node);

    //declaration
    void visit(FunctionDeclarationNode node);

    void visit(ClassDeclarationNode node);

    void visit(VariableDeclarationNode node);

    //statement
    void visit(WhileStatementNode node);

    void visit(ForStatementNode node);

    void visit(BreakStatementNode node);

    void visit(ContinueStatementNode node);

    void visit(ReturnStatementNode node);

    void visit(ConditionStatementNode node);

    void visit(ExpressionStatementNode node);

    void visit(BlockStatementNode node);

    //expression
    void visit(VariableListExpressionNode node);

    void visit(TypeExpressionNode node);

    void visit(ThisExpressionNode node);

    void visit(BinaryExpressionNode node);

    void visit(AssignExpressionNode node);

    void visit(NewExpressionNode node);

    void visit(SubscriptExpressionNode node);

    void visit(PrefixExpressionNode node);

    void visit(BoolConstantExpressionNode node);
    void visit(IntConstantExpressionNode node);
    void visit(StringConstantExpressionNode node);

    void visit(MemberAccessExpressionNode node);

    void visit(NullExpressionNode node);

    void visit(SuffixExpressionNode node);

    void visit(FunctionCallExpressionNode node);

    void visit(IdentifierExpressionNode node);
}
