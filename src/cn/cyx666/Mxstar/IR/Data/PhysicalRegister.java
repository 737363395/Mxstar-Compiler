package cn.cyx666.Mxstar.IR.Data;

import cn.cyx666.Mxstar.IR.*;

public class PhysicalRegister extends Register {
    private String name;
    private boolean isGeneral, isCallerSave, isCalleeSave;
    private int arg6Index;

    public PhysicalRegister(String name, boolean isGeneral, boolean isCallerSave, boolean isCalleeSave, int arg6Index) {
        this.name = name;
        this.isGeneral = isGeneral;
        this.isCallerSave = isCallerSave;
        this.isCalleeSave = isCalleeSave;
        this.arg6Index = arg6Index;
    }

    public String getName() {
        return name;
    }

    public int getArg6Index() {
        return arg6Index;
    }

    public boolean isGeneral() {
        return isGeneral;
    }

    public boolean isCalleeSave() {
        return isCalleeSave;
    }

    public boolean isCallerSave() {
        return isCallerSave;
    }

    public boolean isArg6() {
        return arg6Index != -1;
    }

    @Override
    public Data copy() {
        return null;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
