package cn.cyx666.Mxstar.IR.Data;

import cn.cyx666.Mxstar.IR.*;

public abstract class Register extends Data {
    public abstract void accept(IRVisitor visitor);

}
