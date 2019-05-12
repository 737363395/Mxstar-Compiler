package cn.cyx666.Mxstar.AST.ExpressionNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.Type.*;

abstract public class ExpressionNode extends ASTNode {
    private Type type;
    private boolean isLeftValue;
    private Data data, address;
    private int addressOffset;
    private BasicBlock trueBasicBlock, falseBasicBlock;

    public void setType(Type type) {
        this.type = type;
    }

    public void setLeftValue(boolean leftValue) {
        isLeftValue = leftValue;
    }

    public Type getType() {
        return type;
    }

    public boolean isLeftValue() {
        return isLeftValue;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getAddress() {
        return address;
    }

    public void setAddress(Data address) {
        this.address = address;
    }

    public int getAddressOffset() {
        return addressOffset;
    }

    public void setAddressOffset(int addressOffset) {
        this.addressOffset = addressOffset;
    }

    public BasicBlock getTrueBasicBlock() {
        return trueBasicBlock;
    }

    public void setTrueBasicBlock(BasicBlock trueBasicBlock) {
        this.trueBasicBlock = trueBasicBlock;
    }

    public BasicBlock getFalseBasicBlock() {
        return falseBasicBlock;
    }

    public void setFalseBasicBlock(BasicBlock falseBasicBlock) {
        this.falseBasicBlock = falseBasicBlock;
    }
}
