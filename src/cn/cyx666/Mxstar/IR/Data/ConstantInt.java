package cn.cyx666.Mxstar.IR.Data;

import cn.cyx666.Mxstar.IR.*;

public class ConstantInt extends Data {
    int value;

    public ConstantInt(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public ConstantInt copy() {
        return new ConstantInt(value);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
