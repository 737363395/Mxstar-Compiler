package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.*;

import java.util.Map;

public class HeapAllocateInstruction extends IRInstruction {
    private Register destination;
    private Data size;

    public HeapAllocateInstruction(BasicBlock parentBasicBlock, Register destination, Data size){
        super(parentBasicBlock);
        this.destination = destination;
        this.size = size;
        updateData();
    }

    public void updateData() {
        registerList.clear();
        dataList.clear();
        if (size instanceof Register) registerList.add((Register)size);
        dataList.add(size);
    }

    public Register getDestination() {
        return destination;
    }

    public Data getSize() {
        return size;
    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        return new HeapAllocateInstruction((BasicBlock) renameMap.getOrDefault(getParentBasicBlock(), getParentBasicBlock()),
                (Register) renameMap.getOrDefault(destination, destination),
                (Data) renameMap.getOrDefault(size, size));
    }

    @Override
    public void setRegister(Map<Register, Register> renameMap) {
        if (size instanceof Register) size = renameMap.get(size);
        updateData();
    }

    @Override
    public Register getDefinedRegister() {
        return destination;
    }

    @Override
    public void setDefinedRegister(Register register) {
        destination = register;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
