package cn.cyx666.Mxstar.AST;

public abstract class ASTNode {
    protected Position position;

    public ASTNode() {}

    public Position position() {
        return position;
    }

    abstract public void accept(ASTVisitor visitor);
}