package cn.cyx666.Mxstar.AST.StatementNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;

public class ConditionStatementNode extends StatementNode {
    private ExpressionNode condition;
    private StatementNode thenStatement, elseStatement;

    public ConditionStatementNode(ExpressionNode condition, StatementNode thenStatement, StatementNode elseStatement, Position position){
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
        this.position = position;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public StatementNode getElseStatement() {
        return elseStatement;
    }

    public StatementNode getThenStatement() {
        return thenStatement;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
