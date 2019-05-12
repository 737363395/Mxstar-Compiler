package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;

public class AssignExpressionNode extends ExpressionNode {
    ExpressionNode leftChild, rightChild;

    public AssignExpressionNode(ExpressionNode leftChild, ExpressionNode rightChild, Position position) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.position = position;
    }

    public ExpressionNode getLeftChild() {
        return leftChild;
    }

    public ExpressionNode getRightChild() {
        return rightChild;
    }


    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
