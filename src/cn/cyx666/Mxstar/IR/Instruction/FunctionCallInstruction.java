package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.Function.*;
import cn.cyx666.Mxstar.IR.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionCallInstruction extends IRInstruction {
    private IRFunction function;
    private List<Data> parameters;
    private Register destination;

    public FunctionCallInstruction(BasicBlock parentBasicBlock, IRFunction function, List<Data> parameters, Register destination) {
        super(parentBasicBlock);
        this.function = function;
        this.destination = destination;
        this.parameters = parameters;
        updateData();
    }

    public void updateData(){
        registerList.clear();
        dataList.clear();
        for (Data parameter : parameters) {
            if (parameter instanceof Register) registerList.add((Register) parameter);
            dataList.add(parameter);
        }
    }

    public IRFunction getFunction() {
        return function;
    }

    public List<Data> getParameters() {
        return parameters;
    }

    public Register getDestination() {
        return destination;
    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        List<Data> parametersCopy = new ArrayList<>();
        for (Data parameter: parameters) {
            parametersCopy.add((Data) renameMap.getOrDefault(parameter, parameter));
        }
        return new FunctionCallInstruction((BasicBlock) renameMap.getOrDefault(getParentBasicBlock(), getParentBasicBlock()),
                function,
                parametersCopy,
                (Register) renameMap.getOrDefault(destination, destination));
    }

    @Override
    public void setRegister(Map<Register, Register> renameMap) {
        for (int i = 0; i < parameters.size(); ++i) {
            if (parameters.get(i) instanceof Register) {
                parameters.set(i, renameMap.get((Register) parameters.get(i)));
            }
        }
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
