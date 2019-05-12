package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.BasicBlock;
import cn.cyx666.Mxstar.IR.Data.ConstantData;
import cn.cyx666.Mxstar.IR.Data.Data;
import cn.cyx666.Mxstar.IR.Data.Register;
import cn.cyx666.Mxstar.IR.Data.StackSlot;
import cn.cyx666.Mxstar.IR.IRVisitor;
import cn.cyx666.Mxstar.IR.Instruction.*;

import java.util.Map;

public class StoreInstruction extends IRInstruction {
    private Data data;
    private int size;
    private Data address;
    private int offset;
    private boolean isConstant;

    public StoreInstruction(BasicBlock parentBasicBlock, Data data, int size, Data address, int offset) {
        super(parentBasicBlock);
        this.data = data;
        this.size = size;
        this.address = address;
        this.offset = offset;
        this.isConstant = false;
        updateData();
    }

    public StoreInstruction(BasicBlock parentBasicBlock, Data data, int size, ConstantData address) {
        this(parentBasicBlock, data, size, address, 0);
        this.data = data;
        this.size = size;
        this.address = address;
        this.isConstant = true;
    }

    public void updateData(){
        registerList.clear();
        dataList.clear();
        if (address instanceof Register && !(address instanceof StackSlot)) registerList.add((Register)address);
        if (data instanceof Register) registerList.add((Register) data);
        dataList.add(address);
    }

    public Data getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Data getAddress() {
        return address;
    }

    public void setAddress(Data address) {
        this.address = address;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public boolean equals(LoadInstruction instruction) {
        return data == instruction.getDestination() && size == instruction.getSize() &&
                address == instruction.getAddress() && offset == instruction.getOffset();
    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        return new StoreInstruction((BasicBlock) renameMap.getOrDefault(getParentBasicBlock(), getParentBasicBlock()),
                (Data) renameMap.getOrDefault(data, data),
                size,
                (Data) renameMap.getOrDefault(address, address),
                offset);
    }

    @Override
    public void setRegister(Map<Register, Register> renameMap) {
        if (data instanceof Register) data = renameMap.get(data);
        if (address instanceof Register) address = renameMap.get(address);
        updateData();
    }

    @Override
    public void setDefinedRegister(Register register) {

    }

    @Override
    public Register getDefinedRegister() {
        return null;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
