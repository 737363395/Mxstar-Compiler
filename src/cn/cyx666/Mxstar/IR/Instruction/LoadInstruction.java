package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.IRVisitor;

import java.util.Map;

public class LoadInstruction extends IRInstruction {
    private Register destination;
    private int size;
    private Data address;
    private int offset;
    private boolean isConstant, isAddress;

    public LoadInstruction(BasicBlock parentBasicBlock, Register destination, int size, Data address, int offset) {
        super(parentBasicBlock);
        this.destination = destination;
        this.size = size;
        this.address = address;
        this.offset = offset;
        this.isConstant = false;
        updateData();
    }

    public LoadInstruction(BasicBlock parentBasicBlock, Register destination, int size, ConstantData address, boolean isAddress) {
        this(parentBasicBlock, destination, size, address, 0);
        this.isConstant = true;
        this.isAddress = isAddress;
    }

    private void updateData(){
        registerList.clear();
        dataList.clear();
        if (address instanceof Register && !(address instanceof StackSlot)) registerList.add((Register) address);
        dataList.add(address);
    }

    public Register getDestination() {
        return destination;
    }

    public int getSize() {
        return size;
    }

    public Data getAddress() {
        return address;
    }

    public void setAddress(Data address) {
        this.address = address;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public boolean equals(StoreInstruction instruction) {
        return destination == instruction.getData() && size == instruction.getSize() &&
                address == instruction.getAddress() && offset == instruction.getOffset();

    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        return new LoadInstruction((BasicBlock) renameMap.getOrDefault(getParentBasicBlock(), getParentBasicBlock()),
                (Register) renameMap.getOrDefault(destination, destination),
                size,
                (Data) renameMap.getOrDefault(address, address),
                offset);
    }

    @Override
    public void setRegister(Map<Register, Register> renameMap) {
        if (address instanceof Register && !(address instanceof StackSlot)) address = renameMap.get(address);
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
