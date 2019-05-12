package cn.cyx666.Mxstar.AST.ProgramNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.DeclarationNode.*;

import java.util.List;

public class ProgramNode extends ASTNode {
    private List<DeclarationNode> declarations;

    public ProgramNode(List<DeclarationNode> declarations, Position position){
        this.declarations = declarations;
        this.position = position;
    }

    public List<DeclarationNode> getDeclarations() {
        return declarations;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
