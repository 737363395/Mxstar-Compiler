package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;

public class PrefixExpressionNode extends ExpressionNode {

    public enum PrefixOperators {
        PREFIX_INC, PREFIX_DEC, POS, NEG, LOGIC_NOT, BITWISE_NOT
    }

    private PrefixOperators operator;
    private ExpressionNode expression;

    public PrefixExpressionNode(PrefixOperators operator, ExpressionNode expression, Position position){
        this.operator = operator;
        this.expression = expression;
        this.position = position;
    }

    public PrefixOperators getOperator() {
        return operator;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
