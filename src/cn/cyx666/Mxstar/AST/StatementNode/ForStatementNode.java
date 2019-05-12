package cn.cyx666.Mxstar.AST.StatementNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;

public class ForStatementNode extends LoopStatementNode {
    private ExpressionNode initialization, condition, step;
    private StatementNode statements;

    public ForStatementNode(ExpressionNode initialization, ExpressionNode condition, ExpressionNode step, StatementNode statements, Position position){
        this.initialization = initialization;
        this.condition = condition;
        this.step = step;
        this.statements = statements;
        this.position = position;
    }

    public ExpressionNode getInitialization() {
        return initialization;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public ExpressionNode getStep() {
        return step;
    }

    public StatementNode getStatements() {
        return statements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
