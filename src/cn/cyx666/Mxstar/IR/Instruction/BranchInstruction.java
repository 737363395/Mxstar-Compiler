package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.*;

import java.util.Map;

public class BranchInstruction extends JumpInstruction {
    Data condition;
    BasicBlock thenBasicBlock, elseBasicBlock;

    public BranchInstruction(BasicBlock parentBasicBlock, Data condition, BasicBlock thenBasicBlock, BasicBlock elseBasicBlock) {
        super(parentBasicBlock);
        this.condition = condition;
        this.thenBasicBlock = thenBasicBlock;
        this.elseBasicBlock = elseBasicBlock;
        updateData();
    }

    private void updateData() {
        dataList.clear();
        registerList.clear();
        if (condition instanceof Register) registerList.add((Register) condition);
        dataList.add(condition);
    }

    public Data getCondition() {
        return condition;
    }

    public BasicBlock getThenBasicBlock() {
        return thenBasicBlock;
    }

    public void setThenBasicBlock(BasicBlock thenBasicBlock) {
        this.thenBasicBlock = thenBasicBlock;
    }

    public BasicBlock getElseBasicBlock() {
        return elseBasicBlock;
    }

    public void setElseBasicBlock(BasicBlock elseBasicBlock) {
        this.elseBasicBlock = elseBasicBlock;
    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        return new BranchInstruction((BasicBlock) renameMap.getOrDefault(getParentBasicBlock(), getParentBasicBlock()),
                (Data) renameMap.getOrDefault(condition, condition),
                (BasicBlock) renameMap.getOrDefault(thenBasicBlock, thenBasicBlock),
                (BasicBlock) renameMap.getOrDefault(elseBasicBlock, elseBasicBlock));
    }

    @Override
    public void setRegister(Map<Register, Register> renameMap) {
        if (condition instanceof Register) condition = renameMap.get(condition);
        updateData();
    }

    @Override
    public void setDefinedRegister(Register register) {

    }

    @Override
    public Register getDefinedRegister() {
        return null;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
