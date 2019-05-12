package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;

import java.util.List;

public class NewExpressionNode extends ExpressionNode {
    private TypeExpressionNode newType;
    private List<ExpressionNode> dimensions;
    private int dimensionCount;

    public NewExpressionNode(TypeExpressionNode newType, List<ExpressionNode> dimensions, int dimensionCount, Position position) {
        this.newType = newType;
        this.dimensions = dimensions;
        this.dimensionCount = dimensionCount;
        this.position = position;
    }

    public TypeExpressionNode getNewType() {
        return newType;
    }

    public List<ExpressionNode> getDimensions() {
        return dimensions;
    }

    public int getDimensionCount() {
        return dimensionCount;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
