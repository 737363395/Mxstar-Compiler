package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.ASTVisitor;
import cn.cyx666.Mxstar.AST.Position;

public class BoolConstantExpressionNode extends ConstantExpressionNode {
    private boolean value;

    public BoolConstantExpressionNode(boolean value, Position position){
        this.value = value;
        this.position = position;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
