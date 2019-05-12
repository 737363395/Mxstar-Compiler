package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;

public class IntConstantExpressionNode extends ConstantExpressionNode {
    private int value;

    public IntConstantExpressionNode(int value, Position position){
        this.value = value;
        this.position = position;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
