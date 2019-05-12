package cn.cyx666.Mxstar.Scope.Entity;

import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.Type.*;
import cn.cyx666.Mxstar.AST.DeclarationNode.*;

public class VariableEntity extends Entity{
    private boolean isGlobal = false;
    private boolean isUsed = true;
    private Register register;
    private int addressOffset;

    public VariableEntity(String name, Type type) {
        super(name, type);
    }

    public VariableEntity(VariableDeclarationNode node) {
        super(node.getName(), node.getType().getType());
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public int getAddressOffset() {
        return addressOffset;
    }

    public void setAddressOffset(int addressOffset) {
        this.addressOffset = addressOffset;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
            this.register = register;
    }
}
