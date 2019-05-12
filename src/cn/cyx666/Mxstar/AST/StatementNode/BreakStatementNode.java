package cn.cyx666.Mxstar.AST.StatementNode;

import cn.cyx666.Mxstar.AST.*;

public class BreakStatementNode extends JumpStatementNode {
    public BreakStatementNode(Position position){
        this.position = position;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
