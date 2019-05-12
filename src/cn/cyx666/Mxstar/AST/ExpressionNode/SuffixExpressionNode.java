package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;

public class SuffixExpressionNode extends ExpressionNode {
    public enum SuffixOperators {
        SUFFIX_INC, SUFFIX_DEC
    }

    private SuffixOperators operator;
    private ExpressionNode expression;

    public SuffixExpressionNode(SuffixOperators operator, ExpressionNode expression, Position position){
        this.operator = operator;
        this.expression = expression;
        this.position = position;
    }

    public SuffixOperators getOperator() {
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
