package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.DeclarationNode.*;

import java.util.List;

public class VariableListExpressionNode extends ASTNode {
    private List<VariableDeclarationNode> declarations;

    public VariableListExpressionNode(List<VariableDeclarationNode> declarations){
        this.declarations = declarations;
    }

    public List<VariableDeclarationNode> getDeclarations() {
        return declarations;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
