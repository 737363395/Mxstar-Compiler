package cn.cyx666.Mxstar.IR.BasicBlock;

import cn.cyx666.Mxstar.IR.Function.*;
import cn.cyx666.Mxstar.IR.IRVisitor;
import cn.cyx666.Mxstar.IR.Instruction.*;

import java.util.HashSet;
import java.util.Set;

public class BasicBlock {
    private IRInstruction firstInstruction = null, lastInstruction = null;
    private IRFunction function;
    private String name;
    private boolean isJump = false;
    private int index;
    private Set<BasicBlock> outBasicBlock = new HashSet<>();

    public BasicBlock(IRFunction function, String name) {
        this.function = function;
        this.name = name;
    }

    public IRFunction getFunction() {
        return function;
    }

    public String getName() {
        return name;
    }

    public IRInstruction getFirstInstruction() {
        return firstInstruction;
    }

    public void setFirstInstruction(IRInstruction firstInstruction) {
        this.firstInstruction = firstInstruction;
    }

    public IRInstruction getLastInstruction() {
        return lastInstruction;
    }

    public void setLastInstruction(IRInstruction lastInstruction) {
        this.lastInstruction = lastInstruction;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Set<BasicBlock> getOutBasicBlock() {
        return outBasicBlock;
    }

    public void addInstruction(IRInstruction instruction) {
        if (firstInstruction == null) {
            firstInstruction = lastInstruction = instruction;
        }else{
            lastInstruction.linkNext(instruction);
            lastInstruction = instruction;
        }
        function.addInstructionCount(1);
        if (instruction instanceof FunctionCallInstruction) {
            ((FunctionCallInstruction) instruction).getFunction().addCalledCounted(1);
        }
    }


    private void addOutBasicBlock(BasicBlock basicBlock) {
        outBasicBlock.add(basicBlock);
    }


    private void delOutBasicBlock(BasicBlock basicBlock) {
        outBasicBlock.remove(basicBlock);
    }

    public void setJumpInstruction(JumpInstruction instruction){
        addInstruction(instruction);
        isJump = true;
        if (instruction instanceof BranchInstruction) {
            addOutBasicBlock(((BranchInstruction) instruction).getThenBasicBlock());
            addOutBasicBlock(((BranchInstruction) instruction).getElseBasicBlock());
        } else if (instruction instanceof GotoInstruction) {
            addOutBasicBlock(((GotoInstruction) instruction).getBasicBlock());
        } else if (instruction instanceof ReturnInstruction) {
            function.getReturnList().add((ReturnInstruction) instruction);
        }
    }

    public void reConstruct() {
        firstInstruction = null;
        lastInstruction = null;
        isJump = false;
    }

    public void removeJumpInstruction(){
        isJump = false;
        if (lastInstruction instanceof BranchInstruction) {
            delOutBasicBlock(((BranchInstruction) lastInstruction).getThenBasicBlock());
            delOutBasicBlock(((BranchInstruction) lastInstruction).getElseBasicBlock());
        } else if (lastInstruction instanceof GotoInstruction) {
            delOutBasicBlock(((GotoInstruction) lastInstruction).getBasicBlock());
        } else if (lastInstruction instanceof ReturnInstruction) {
            function.getReturnList().remove((ReturnInstruction) lastInstruction);
        }
    }

    public boolean isJump() {
        return isJump;
    }

    public void setJump(boolean jump) {
        isJump = jump;
    }

    public void simplifyInstruction(){
        IRInstruction lastInstruction = null;
        for (IRInstruction instruction = firstInstruction; instruction != null; instruction = instruction.getNextInstruction()) {
            if (instruction instanceof MoveInstruction) {
                instruction.setAvaiable(((MoveInstruction) instruction).getData() != ((MoveInstruction) instruction).getDestination());
                if (instruction.isAvaiable() && lastInstruction instanceof MoveInstruction) {
                    instruction.setAvaiable(!(((MoveInstruction) instruction).getData() == ((MoveInstruction) lastInstruction).getDestination() &&
                            ((MoveInstruction) instruction).getDestination() == ((MoveInstruction) lastInstruction).getData()));
                }
            } else if (instruction instanceof LoadInstruction && lastInstruction instanceof StoreInstruction) {
                instruction.setAvaiable(!instruction.equals(lastInstruction));
            } else if (instruction instanceof StoreInstruction && lastInstruction instanceof LoadInstruction) {
                instruction.setAvaiable(!instruction.equals(lastInstruction));
            }
            if (instruction.isAvaiable()) lastInstruction = instruction;
            else instruction.remove();
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
