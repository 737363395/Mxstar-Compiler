package cn.cyx666.Mxstar.AST.StatementNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.Scope.*;

import java.util.List;

public class BlockStatementNode extends StatementNode {
    private List<ASTNode> statements;
    Scope scope;

    public BlockStatementNode(List<ASTNode> statements, Position position){
        this.statements = statements;
        this.position = position;
    }

    public void initializeScope(Scope parent) {
        scope = new Scope(parent, false);
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
