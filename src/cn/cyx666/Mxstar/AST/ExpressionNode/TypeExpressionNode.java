package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.Type.*;

public class TypeExpressionNode extends ASTNode {
    private Type type;

    public TypeExpressionNode(Type type, Position position) {
        this.type = type;
        this.position = position;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
