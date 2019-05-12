package cn.cyx666.Mxstar.IR.Data;

import cn.cyx666.Mxstar.Configuration.*;
import cn.cyx666.Mxstar.IR.*;

public class ConstantString extends ConstantData{
    String string;

    public ConstantString(String string) {
        super("string", Configuration.getRegisterSize());
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
