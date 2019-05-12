package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.*;

import java.util.Map;

public class ReturnInstruction extends JumpInstruction {
    private Data data;

    public ReturnInstruction(BasicBlock parentBasicBlock, Data data) {
        super(parentBasicBlock);
        this.data = data;
        updateData();
    }

    private void updateData() {
        registerList.clear();
        dataList.clear();
        if (data != null && data instanceof Register) registerList.add((Register) data);
        if (data != null) dataList.add(data);
    }

    public Data getData() {
        return data;
    }

    @Override
    public IRInstruction copyRename(Map<Object, Object> renameMap) {
        return new ReturnInstruction((BasicBlock) renameMap.getOrDefault(getParentBasicBlock(), getParentBasicBlock()),
                (Data) renameMap.getOrDefault(data, data));
    }

    @Override
    public void setDefinedRegister(Register register) {

    }

    @Override
    public void setRegister(Map<Register, Register> renameMap) {
        if (data instanceof Register) data = renameMap.get(data);
        updateData();
    }

    @Override
    public Register getDefinedRegister() {
        return null;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
