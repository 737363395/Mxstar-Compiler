package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;

public class StringConstantExpressionNode extends ConstantExpressionNode {
    private String value;

    public StringConstantExpressionNode(String value, Position position){
        this.value = value;
        this.position = position;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
