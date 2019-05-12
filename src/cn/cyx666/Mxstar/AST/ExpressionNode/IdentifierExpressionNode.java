package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.Scope.Entity.*;

public class IdentifierExpressionNode extends ExpressionNode {
    private String identifier;
    private VariableEntity entity = null;
    private boolean needMemory = false;

    public IdentifierExpressionNode(String identifier, Position position){
        this.identifier = identifier;
        this.position = position;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setEntity(VariableEntity entity) {
        this.entity = entity;
    }

    public VariableEntity getEntity() {
        return entity;
    }

    public boolean isNeedMemory() {
        return needMemory;
    }

    public void setNeedMemory(boolean needMemory) {
        this.needMemory = needMemory;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
