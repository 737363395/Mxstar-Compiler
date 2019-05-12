package cn.cyx666.Mxstar.AST.StatementNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;

public class ExpressionStatementNode extends StatementNode{
    ExpressionNode expression;

    public ExpressionStatementNode(ExpressionNode expression, Position position){
        this.expression = expression;
        this.position = position;
    }

    public ExpressionStatementNode(ExpressionNode expression){
        this.expression = expression;
        this.position =expression.position();
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
