package cn.cyx666.Mxstar.IR.HyperBlock;

import cn.cyx666.Mxstar.IR.BasicBlock.*;

import java.util.ArrayList;
import java.util.List;

public class ForHyperBlock {
    private BasicBlock conditionBasicBlock, stepBasicBlock, blockBasicBlock, nextBasicBlock;
    private List<BasicBlock> basicBlockList;

    public ForHyperBlock(BasicBlock conditionBasicBlock, BasicBlock stepBasicBlock, BasicBlock blockBasicBlock, BasicBlock nextBasicBlock) {
        this.conditionBasicBlock = conditionBasicBlock;
        this.stepBasicBlock = stepBasicBlock;
        this.blockBasicBlock = blockBasicBlock;
        this.nextBasicBlock = nextBasicBlock;
        basicBlockList = new ArrayList<>();
        basicBlockList.add(conditionBasicBlock);
        basicBlockList.add(stepBasicBlock);
        basicBlockList.add(blockBasicBlock);
        basicBlockList.add(nextBasicBlock);
    }

    public void replace(BasicBlock oldBasicBlock, BasicBlock newBasicBlock) {
        if (oldBasicBlock == conditionBasicBlock) {
            conditionBasicBlock = newBasicBlock;
        }
        if (oldBasicBlock == stepBasicBlock) {
            stepBasicBlock = newBasicBlock;
        }
        if (oldBasicBlock == blockBasicBlock) {
            blockBasicBlock = newBasicBlock;
        }
        if (oldBasicBlock == nextBasicBlock) {
            nextBasicBlock = newBasicBlock;
        }
    }

    public BasicBlock getBlockBasicBlock() {
        return blockBasicBlock;
    }

    public void setBlockBasicBlock(BasicBlock blockBasicBlock) {
        this.blockBasicBlock = blockBasicBlock;
    }

    public BasicBlock getConditionBasicBlock() {
        return conditionBasicBlock;
    }

    public void setConditionBasicBlock(BasicBlock conditionBasicBlock) {
        this.conditionBasicBlock = conditionBasicBlock;
    }

    public BasicBlock getNextBasicBlock() {
        return nextBasicBlock;
    }

    public void setNextBasicBlock(BasicBlock nextBasicBlock) {
        this.nextBasicBlock = nextBasicBlock;
    }

    public BasicBlock getStepBasicBlock() {
        return stepBasicBlock;
    }

    public void setStepBasicBlock(BasicBlock stepBasicBlock) {
        this.stepBasicBlock = stepBasicBlock;
    }

    public List<BasicBlock> getBasicBlockList() {
        return basicBlockList;
    }
}
