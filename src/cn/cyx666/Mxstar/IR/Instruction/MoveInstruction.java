package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.*;

import java.util.Map;

public class MoveInstruction extends IRInstruction {
    private Register destination;
    private Data data;

    public MoveInstruction(BasicBlock parentBasicBlock, Register destination, Data data){
        super(parentBasicBlock);
        this.destination = destination;
        this.data = data;
        updateData();
    }

    private void updateData() {
        registerList.clear();
        dataList.clear();
        if (data instanceof Register) registerList.add((Register) data);
        dataList.add(data);
    }

    public Register getDestination() {
        return destination;
    }

    public Data getData() {
        return data;
    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        return new MoveInstruction((BasicBlock) renameMap.getOrDefault(getParentBasicBlock(), getParentBasicBlock()),
                (Register) renameMap.getOrDefault(destination, destination),
                (Data) renameMap.getOrDefault(data, data));
    }

    @Override
    public void setRegister(Map<Register, Register> renameMap) {
        if (data instanceof Register) data = renameMap.get(data);
        updateData();
    }

    @Override
    public void setDefinedRegister(Register register) {
        destination = register;
    }

    @Override
    public Register getDefinedRegister() {
        return destination;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
