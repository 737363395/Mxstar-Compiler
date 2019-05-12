package cn.cyx666.Mxstar.AST.StatementNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;
public class WhileStatementNode extends LoopStatementNode {
    private ExpressionNode condition;
    private StatementNode statement;

    public WhileStatementNode(ExpressionNode condition, StatementNode statement, Position position){
        this.condition = condition;
        this.statement = statement;
        this.position = position;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public StatementNode getStatement() {
        return statement;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
