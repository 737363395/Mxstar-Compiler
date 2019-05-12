package cn.cyx666.Mxstar.AST.StatementNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;

public class ReturnStatementNode extends StatementNode {
    ExpressionNode expression;

    public ReturnStatementNode(ExpressionNode expression, Position position){
        this.expression = expression;
        this.position = position;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
