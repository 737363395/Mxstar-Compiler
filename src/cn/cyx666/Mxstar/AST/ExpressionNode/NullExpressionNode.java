package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;

public class NullExpressionNode extends ExpressionNode {
    public NullExpressionNode(Position position){
        this.position = position;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
