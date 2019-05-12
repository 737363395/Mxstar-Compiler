package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;

public class SubscriptExpressionNode extends ExpressionNode {
    ExpressionNode array, subscript;

    public SubscriptExpressionNode(ExpressionNode array, ExpressionNode subscript, Position position){
        this.array = array;
        this.subscript = subscript;
        this.position = position;
    }

    public ExpressionNode getArray() {
        return array;
    }

    public ExpressionNode getSubscript() {
        return subscript;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
