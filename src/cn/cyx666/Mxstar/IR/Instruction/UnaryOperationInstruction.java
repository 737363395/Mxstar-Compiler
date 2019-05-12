package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.*;

import java.util.Map;

public class UnaryOperationInstruction extends IRInstruction{
    public enum UnaryOperator {
        BITWISE_NOT, NEG
    }

    private Register destination;
    private UnaryOperator operator;
    private Data data;

    public UnaryOperationInstruction(BasicBlock parentBasicBlock, Register destination, UnaryOperator operator, Data data) {
        super(parentBasicBlock);
        this.destination = destination;
        this.operator = operator;
        this.data = data;
        updateData();
    }

    private void updateData() {
        registerList.clear();
        dataList.clear();
        if (data instanceof  Register) registerList.add((Register) data);
        dataList.add(data);
    }

    public Register getDestination() {
        return destination;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    public Data getData() {
        return data;
    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        return new UnaryOperationInstruction((BasicBlock) renameMap.getOrDefault(getParentBasicBlock(), getParentBasicBlock()),
                (Register) renameMap.getOrDefault(destination, destination),
                operator,
                (Data) renameMap.getOrDefault(data, data));
    }

    @Override
    public void setRegister(Map<Register, Register> renameMap) {
        if (data instanceof Register) data =  renameMap.get(data);
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
