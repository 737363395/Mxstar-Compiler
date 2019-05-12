package cn.cyx666.Mxstar.IR.Instruction;


import cn.cyx666.Mxstar.IR.BasicBlock.*;

public abstract class JumpInstruction extends IRInstruction {

    public JumpInstruction(BasicBlock parentBasicBlock) {
        super(parentBasicBlock);
    }
}
