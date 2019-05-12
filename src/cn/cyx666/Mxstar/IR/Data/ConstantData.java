package cn.cyx666.Mxstar.IR.Data;

import cn.cyx666.Mxstar.IR.*;

public abstract class ConstantData extends Register {
    String name;
    int size;

    public ConstantData(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    @Override
    public ConstantData copy() {
        return this;
    }

    public abstract void accept(IRVisitor visitor);
}
