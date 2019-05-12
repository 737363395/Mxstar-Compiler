package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.ASTVisitor;
import cn.cyx666.Mxstar.AST.Position;

public class BinaryExpressionNode extends ExpressionNode {
    public enum BinaryOperators {
        MUL, DIV, MOD,
        ADD, SUB, SHL, SHR,
        GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, EQUAL, INEQUAL,
        BITWISE_AND, BITWISE_OR, BITWISE_XOR, LOGIC_AND, LOGIC_OR
    }

    private BinaryOperators operator;
    private ExpressionNode leftChild, rightChild;

    public BinaryExpressionNode(BinaryOperators operator, ExpressionNode leftChild, ExpressionNode rightChild, Position position){
        this.operator = operator;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.position = position;
    }

    public BinaryOperators getOperator() {
        return operator;
    }

    public ExpressionNode getLeftChild() {
        return leftChild;
    }

    public ExpressionNode getRightChild() {
        return rightChild;
    }

    public void setLeftChild(ExpressionNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(ExpressionNode rightChild) {
        this.rightChild = rightChild;
    }

    public void swap() {
        ExpressionNode node = leftChild;
        leftChild = rightChild;
        rightChild = node;
        switch (operator) {
            case LESS:
                operator = BinaryOperators.GREATER;
                break;
            case GREATER:
                operator = BinaryOperators.LESS;
                break;
            case LESS_EQUAL:
                operator = BinaryOperators.GREATER_EQUAL;
                break;
            case GREATER_EQUAL:
                operator = BinaryOperators.LESS_EQUAL;
                break;
        }
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
