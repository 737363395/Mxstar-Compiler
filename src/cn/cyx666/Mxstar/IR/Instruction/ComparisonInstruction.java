package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.*;

import java.util.Map;

public class ComparisonInstruction extends IRInstruction {
    public enum ComparisionOperator{
        GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, EQUAL, INEQUAL
    }

    private Register destination;
    private ComparisionOperator operator;
    private Data leftData, rightData;

    public ComparisonInstruction(BasicBlock parentBasicBlock, Register destination, ComparisionOperator operator, Data leftData, Data rightData) {
        super(parentBasicBlock);
        this.destination = destination;
        this.operator = operator;
        this.leftData = leftData;
        this.rightData = rightData;
        updateData();
    }

    private void updateData() {
        dataList.clear();
        registerList.clear();
        if (leftData instanceof Register) registerList.add((Register) leftData);
        if (rightData instanceof Register) registerList.add((Register) rightData);
        dataList.add(leftData);
        dataList.add(rightData);
    }

    public Register getDestination() {
        return destination;
    }

    public ComparisionOperator getOperator() {
        return operator;
    }

    public Data getLeftData() {
        return leftData;
    }

    public Data getRightData() {
        return rightData;
    }

    private ComparisionOperator getOpponentOperator(ComparisionOperator operator) {
        switch (operator) {
            case LESS:
                return ComparisionOperator.GREATER;
            case GREATER:
                return ComparisionOperator.LESS;
            case LESS_EQUAL:
                return ComparisionOperator.GREATER_EQUAL;
            case GREATER_EQUAL:
                return ComparisionOperator.LESS_EQUAL;
        }
        return operator;
    }

    public void swap() {
        Data data = leftData;
        leftData = rightData;
        rightData = data;
        operator = getOpponentOperator(operator);
    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        return new ComparisonInstruction((BasicBlock) renameMap.getOrDefault(getParentBasicBlock(), getParentBasicBlock()),
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
