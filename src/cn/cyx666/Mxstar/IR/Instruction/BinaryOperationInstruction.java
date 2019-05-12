package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.*;

import java.util.Map;

public class BinaryOperationInstruction extends IRInstruction {
    public enum BinaryOperator {
        ADD, SUB, MUL, DIV, MOD,
        SHL, SHR,
        BITWISE_AND, BITWISE_OR, BITWISE_XOR
    }

    private Register destination;
    private BinaryOperator operator;
    private Data leftData, rightData;

    public BinaryOperationInstruction(BasicBlock parentBasicBlock, Register destination, BinaryOperator operator, Data leftData, Data rightData) {
        super(parentBasicBlock);
        this.destination = destination;
        this.operator = operator;
        this.leftData = leftData;
        this.rightData = rightData;
        updateData();
    }

    private void updateData() {
        registerList.clear();
        dataList.clear();
        if (leftData instanceof Register) registerList.add((Register) leftData);
        if (rightData instanceof Register) registerList.add((Register) rightData);
        dataList.add(leftData);
        dataList.add(rightData);
    }

    public Register getDestination() {
        return destination;
    }

    public BinaryOperator getOperator() {
        return operator;
    }

    public Data getLeftData() {
        return leftData;
    }

    public Data getRightData() {
        return rightData;
    }

    public void setLeftData(Data leftData) {
        this.leftData = leftData;
        updateData();
    }

    public void setRightData(Data rightData) {
        this.rightData = rightData;
        updateData();
    }

    public boolean isCommutative() {
        return operator == BinaryOperator.ADD || operator == BinaryOperator.MUL || operator == BinaryOperator.BITWISE_OR
                || operator == BinaryOperator.BITWISE_AND || operator == BinaryOperator.BITWISE_XOR;
    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        return new BinaryOperationInstruction((BasicBlock) renameMap.getOrDefault(getParentBasicBlock(), getParentBasicBlock()),
                (Register) renameMap.getOrDefault(destination, destination),
                operator,
                (Data) renameMap.getOrDefault(leftData, leftData),
                (Data) renameMap.getOrDefault(rightData, rightData));
    }

    @Override
    public void setRegister(Map<Register, Register> renameMap) {
        if (leftData instanceof Register) leftData =  renameMap.get(leftData);
        if (rightData instanceof Register) rightData =  renameMap.get(rightData);
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
