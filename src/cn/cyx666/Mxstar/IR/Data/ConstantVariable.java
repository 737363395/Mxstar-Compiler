package cn.cyx666.Mxstar.IR.Data;

import cn.cyx666.Mxstar.IR.*;

public class ConstantVariable extends ConstantData {
    public ConstantVariable(String name, int size) {
        super(name, size);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
