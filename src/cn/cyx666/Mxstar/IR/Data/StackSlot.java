package cn.cyx666.Mxstar.IR.Data;

import cn.cyx666.Mxstar.IR.Function.*;
import cn.cyx666.Mxstar.IR.IRVisitor;

public class StackSlot extends Register {
    private IRFunction function;
    private String name;

    public StackSlot(IRFunction function, String name, boolean isParameter) {
        this.function = function;
        this.name = name;
        if (!isParameter) function.getStackSlotList().add(this);
    }

    @Override
    public void accept(IRVisitor visitor) {

    }

    @Override
    public Data copy() {
        return null;
    }
}
