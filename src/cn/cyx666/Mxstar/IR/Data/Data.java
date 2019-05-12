package cn.cyx666.Mxstar.IR.Data;

import cn.cyx666.Mxstar.IR.*;

public abstract class Data {
    public abstract Data copy();

    public abstract void accept(IRVisitor visitor);
}
