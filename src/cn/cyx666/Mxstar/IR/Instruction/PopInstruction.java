package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.*;

import java.util.Map;

public class PopInstruction extends IRInstruction {
    private PhysicalRegister register;

    public PopInstruction(BasicBlock parentBasicBlock, PhysicalRegister register){
        super(parentBasicBlock);
        this.register = register;
    }

    public PhysicalRegister getRegister() {
        return register;
    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        return null;
    }

    @Override
    public void setDefinedRegister(Register register) {

    }

    @Override
    public void setRegister(Map<Register, Register> renameMap) {

    }

    @Override
    public Register getDefinedRegister() {
        return null;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
