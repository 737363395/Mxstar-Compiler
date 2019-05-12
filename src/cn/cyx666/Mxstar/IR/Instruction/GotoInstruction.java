package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.*;
import cn.cyx666.Mxstar.IR.Data.Register;

import java.util.Map;

public class GotoInstruction extends JumpInstruction{
    BasicBlock basicBlock;

    public GotoInstruction(BasicBlock parentBasicBlock, BasicBlock basicBlock) {
        super(parentBasicBlock);
        this.basicBlock = basicBlock;
    }

    public BasicBlock getBasicBlock() {
        return basicBlock;
    }

    public void setBasicBlock(BasicBlock basicBlock) {
        this.basicBlock = basicBlock;
    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        return new GotoInstruction((BasicBlock) renameMap.getOrDefault(getParentBasicBlock(), getParentBasicBlock()),
                (BasicBlock) renameMap.getOrDefault(basicBlock, basicBlock));
    }

    @Override
    public void setDefinedRegister(Register register) {

    }

    @Override
    public Register getDefinedRegister() {
        return null;
    }

    @Override
    public void setRegister(Map<Register, Register> renameMap) {

    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
