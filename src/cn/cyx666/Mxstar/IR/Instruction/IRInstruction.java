package cn.cyx666.Mxstar.IR.Instruction;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.*;

import java.util.*;

public abstract class IRInstruction {
    private IRInstruction nextInstruction = null, previousInstruction = null;
    private BasicBlock parentBasicBlock;
    protected List<Register> registerList = new ArrayList<>();
    protected List<Data> dataList = new ArrayList<>();
    private Set<VirtualRegister> liveIn = new HashSet<>(), liveOut = new HashSet<>();
    private boolean avaiable = true;

    public IRInstruction(BasicBlock parentBasicBlock){
        this.parentBasicBlock = parentBasicBlock;
    }

    public BasicBlock getParentBasicBlock() {
        return parentBasicBlock;
    }

    public IRInstruction getNextInstruction() {
        return nextInstruction;
    }

    public void setNextInstruction(IRInstruction nextInstruction) {
        this.nextInstruction = nextInstruction;
    }

    public IRInstruction getPreviousInstruction() {
        return previousInstruction;
    }

    public void setPreviousInstruction(IRInstruction previousInstruction) {
        this.previousInstruction = previousInstruction;
    }

    public List<Register> getRegisterList() {
        return registerList;
    }

    public List<Data> getDataList() {
        return dataList;
    }

    public abstract IRInstruction copyRename(Map<Object, Object> renameMap);

    public void linkPrevious(IRInstruction instruction) {
        previousInstruction = instruction;
        instruction.setNextInstruction(this);
    }

    public void linkNext(IRInstruction instruction) {
        nextInstruction = instruction;
        instruction.setPreviousInstruction(this);
    }

    public void addFirst(IRInstruction instruction) {
        parentBasicBlock.getFunction().addInstructionCount(1);
        if (previousInstruction != null) {
            previousInstruction.linkNext(instruction);
        } else {
            parentBasicBlock.setFirstInstruction(instruction);
        }
        instruction.linkNext(this);
        if (instruction instanceof FunctionCallInstruction) {
            ((FunctionCallInstruction) instruction).getFunction().addCalledCounted(1);
        }
    }

    public void addLast(IRInstruction instruction) {
        parentBasicBlock.getFunction().addInstructionCount(1);
        if (nextInstruction != null) {
            nextInstruction.linkPrevious(instruction);
        } else {
            parentBasicBlock.setLastInstruction(instruction);
        }
        instruction.linkPrevious(this);
        if (instruction instanceof FunctionCallInstruction) {
            ((FunctionCallInstruction) instruction).getFunction().addCalledCounted(1);
        }
    }

    public void remove() {
        parentBasicBlock.getFunction().addInstructionCount(-1);
        if (previousInstruction != null) {
            previousInstruction.setNextInstruction(nextInstruction);
        }
        if (nextInstruction != null) {
            nextInstruction.setPreviousInstruction(previousInstruction);
        }
        if (this instanceof JumpInstruction) {
            parentBasicBlock.removeJumpInstruction();
        }
        if (this == parentBasicBlock.getFirstInstruction()) {
            parentBasicBlock.setFirstInstruction(nextInstruction);
        }
        if (this == parentBasicBlock.getLastInstruction()) {
            parentBasicBlock.setLastInstruction(previousInstruction);
        }
        if (this instanceof FunctionCallInstruction) {
            ((FunctionCallInstruction) this).getFunction().addCalledCounted(-1);
        }
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }

    public void replace(IRInstruction instruction) {
        avaiable = false;
        instruction.setNextInstruction(nextInstruction);
        instruction.setPreviousInstruction(previousInstruction);
        if (nextInstruction != null) nextInstruction.setPreviousInstruction(instruction);
        if (previousInstruction != null) previousInstruction.setNextInstruction(instruction);
        if (this == parentBasicBlock.getFirstInstruction()) parentBasicBlock.setFirstInstruction(instruction);
        if (this == parentBasicBlock.getLastInstruction()) parentBasicBlock.setLastInstruction(instruction);
    }

    public abstract void setRegister(Map<Register, Register> renameMap);

    public abstract void setDefinedRegister(Register register);

    public abstract Register getDefinedRegister();

    public Set<VirtualRegister> getLiveIn() {
        return liveIn;
    }

    public Set<VirtualRegister> getLiveOut() {
        return liveOut;
    }

    public void setAvaiable(boolean avaiable) {
        this.avaiable = avaiable;
    }

    public boolean isAvaiable() {
        return avaiable;
    }

    public abstract void accept(IRVisitor visitor);
}
