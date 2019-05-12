package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.*;

import java.util.Map;

public class PushInstruction extends IRInstruction {
    Data data;

    public PushInstruction(BasicBlock parentBasicBlock, Data data){
        super(parentBasicBlock);
        this.data = data;
    }

    public Data getData() {
        return data;
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
