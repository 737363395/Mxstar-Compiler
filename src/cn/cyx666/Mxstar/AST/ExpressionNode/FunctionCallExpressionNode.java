package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.Scope.Entity.*;

import java.util.List;

public class FunctionCallExpressionNode extends  ExpressionNode {
    private ExpressionNode function;
    private List<ExpressionNode> parameters;
    private FunctionEntity entity;

    public FunctionCallExpressionNode(ExpressionNode function, List<ExpressionNode> parameters, Position position){
        this.function = function;
        this.parameters = parameters;
        this.position = position;
    }

    public ExpressionNode getFunction() {
        return function;
    }

    public List<ExpressionNode> getParameters() {
        return parameters;
    }

    public FunctionEntity getEntity() {
        return entity;
    }

    public void setEntity(FunctionEntity entity) {
        this.entity = entity;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
