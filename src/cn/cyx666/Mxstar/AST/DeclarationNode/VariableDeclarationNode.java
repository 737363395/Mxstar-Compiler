package cn.cyx666.Mxstar.AST.DeclarationNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;

public class VariableDeclarationNode extends  DeclarationNode{
    private TypeExpressionNode type;
    private ExpressionNode initialization;

    public VariableDeclarationNode(TypeExpressionNode type, String name, ExpressionNode initialization, Position position) {
        this.type = type;
        this.name = name;
        this.initialization = initialization;
        this.position = position;
    }

    public TypeExpressionNode getType() {
        return type;
    }

    public ExpressionNode getInitialization() {
        return initialization;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
