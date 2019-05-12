package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;

public class ThisExpressionNode extends ExpressionNode {
    public ThisExpressionNode(Position position) {
        this.position = position;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
