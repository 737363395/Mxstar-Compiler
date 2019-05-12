package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;

public class MemberAccessExpressionNode extends ExpressionNode {
    private ExpressionNode expression;
    private String member;

    public MemberAccessExpressionNode(ExpressionNode expression, String member, Position position){
        this.expression = expression;
        this.member = member;
        this.position = position;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public String getMember() {
        return member;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
