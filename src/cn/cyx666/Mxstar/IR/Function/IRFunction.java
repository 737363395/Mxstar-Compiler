package cn.cyx666.Mxstar.IR.Function;

import cn.cyx666.Mxstar.Configuration.Configuration;
import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.*;
import cn.cyx666.Mxstar.IR.HyperBlock.ForHyperBlock;
import cn.cyx666.Mxstar.IR.Instruction.*;
import cn.cyx666.Mxstar.NASM.NASMRegisterSet;
import cn.cyx666.Mxstar.Scope.Entity.*;

import java.util.*;

public class IRFunction {
    private FunctionEntity entity = null;
    private BasicBlock startBasicBlock = null, endBasicBlock = null;
    private List<VirtualRegister> parameters = new ArrayList<>();
    private String name, builtInCall;
    private boolean isRecursive = false, isBuiltIn = false;
    private List<ReturnInstruction> returnList = new ArrayList<>();
    private Set<PhysicalRegister> registerSet = new HashSet<>();
    private List<BasicBlock> reversePreOrder = null, reversePostOrder = null;
    private Map<VirtualRegister, StackSlot> stackSlotMap = new HashMap<>();
    private List<StackSlot> stackSlotList = new ArrayList<>();
    private Set<IRFunction> calleeSet = new HashSet<>();
    private Set<IRFunction> recursiveCalleeSet = new HashSet<>();
    private Set<BasicBlock> visitedBasicBlock = new HashSet<>();
    private int instructionCount = 0, calledCount = 0;
    private Set<ConstantData> definedConstantData = new HashSet<>();
    private Set<ConstantData> recursiveDefinedConstantData = new HashSet<>();
    private Set<ConstantData> recursiveConstantData = new HashSet<>();
    private Map<ConstantData, VirtualRegister> constantDataMap = new HashMap<>();
    private Set<ForHyperBlock> forHyperBlockSet = new HashSet<>();
    private List<PhysicalRegister> callerRegisterList = new ArrayList<>();
    private List<PhysicalRegister> calleeRegisterList = new ArrayList<>();
    private Set<PhysicalRegister> recursiveRegisterSet = new HashSet<>();
    private Map<StackSlot, Integer> stackSlotOffsetMap = new HashMap<>();
    private int extraParameterNumber, stackSlotNumber = 0;

    static public String IRFunctionName(String className, String functionName) {
        return className + "_" + functionName;
    }

    public IRFunction(){}

    public IRFunction(FunctionEntity entity) {
        this.entity = entity;
        name = entity.isMember() ? IRFunctionName(entity.getClassName(), entity.getName()) : entity.getName();
    }

    public IRFunction(String name, String builtInCall) {
        this.name = name;
        this.builtInCall = builtInCall;
        this.isBuiltIn = true;
    }

    public void updateCalleeSet() {
        calleeSet.clear();
        for (BasicBlock basicBlock : getReversePostOrder()) {
            for (IRInstruction inst = basicBlock.getFirstInstruction(); inst != null; inst = inst.getNextInstruction()) {
                if (inst instanceof FunctionCallInstruction) {
                    calleeSet.add(((FunctionCallInstruction) inst).getFunction());
                }
            }
        }
    }

    private void dfsPreOrder(BasicBlock basicBlock) {
        if (visitedBasicBlock.contains(basicBlock)) return;
        visitedBasicBlock.add(basicBlock);
        reversePreOrder.add(basicBlock);
        for (BasicBlock outBasicBlock : basicBlock.getOutBasicBlock()) {
            dfsPreOrder(outBasicBlock);
        }
    }

    private void dfsPostOrder(BasicBlock basicBlock) {
        if (visitedBasicBlock.contains(basicBlock)) return;
        visitedBasicBlock.add(basicBlock);
        for (BasicBlock outBasicBlock : basicBlock.getOutBasicBlock()) {
            dfsPostOrder(outBasicBlock);
        }
        reversePostOrder.add(basicBlock);
    }

    public void updateReversePreOrder() {
        reversePreOrder = new ArrayList<>();
        visitedBasicBlock.clear();
        dfsPreOrder(startBasicBlock);
        Collections.reverse(reversePreOrder);
    }

    public List<BasicBlock> getReversePreOrder() {
        if (reversePreOrder == null) {
            updateReversePreOrder();
        }
        return reversePreOrder;
    }

    public void updateReversePostOrder() {
        reversePostOrder = new ArrayList<>();
        visitedBasicBlock.clear();
        dfsPostOrder(startBasicBlock);
        for (int i = 0; i < reversePostOrder.size(); ++i) {
            reversePostOrder.get(i).setIndex(i);
        }
        Collections.reverse(reversePostOrder);
    }

    public List<BasicBlock> getReversePostOrder() {
        if (reversePostOrder == null) {
            updateReversePostOrder();
        }
        return reversePostOrder;
    }

    public List<VirtualRegister> getParameters() {
        return parameters;
    }

    public void setParameters(List<VirtualRegister> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(VirtualRegister virtualRegister) {
        parameters.add(virtualRegister);
    }

    public BasicBlock initStartBasicBlock() {
        startBasicBlock = new BasicBlock(this, entity.getName() + ".start");
        return startBasicBlock;
    }

    public BasicBlock getStartBasicBlock() {
        return startBasicBlock;
    }

    public void setStartBasicBlock(BasicBlock startBasicBlock) {
        this.startBasicBlock = startBasicBlock;
    }

    public BasicBlock getEndBasicBlock() {
        return endBasicBlock;
    }

    public void setEndBasicBlock(BasicBlock endBasicBlock) {
        this.endBasicBlock = endBasicBlock;
    }

    public FunctionEntity getEntity() {
        return entity;
    }

    public void setRecursive(boolean recursive) {
        isRecursive = recursive;
    }

    public boolean isRecursive() {
        return isRecursive;
    }

    public List<ReturnInstruction> getReturnList() {
        return returnList;
    }

    public List<StackSlot> getStackSlotList() {
        return stackSlotList;
    }

    public Map<VirtualRegister, StackSlot> getStackSlotMap() {
        return stackSlotMap;
    }

    public boolean isBuiltIn() {
        return isBuiltIn;
    }

    public String getBuiltInCall() {
        return builtInCall;
    }

    public Set<PhysicalRegister> getRegisterSet() {
        return registerSet;
    }

    public Set<IRFunction> getRecursiveCalleeSet() {
        return recursiveCalleeSet;
    }

    public Set<IRFunction> getCalleeSet() {
        return calleeSet;
    }

    public String getName() {
        return name;
    }

    public BasicBlock initializeFirstBasicBlock() {
        startBasicBlock = new BasicBlock(this, entity.getName() + "_entry");
        endBasicBlock = startBasicBlock;
        return startBasicBlock;
    }

    public void addInstructionCount(int n) {
        instructionCount += n;
    }

    public int getInstructionCount() {
        return instructionCount;
    }

    public void addCalledCounted(int n) {
        calledCount += n;
    }

    public int getCalledCount() {
        return calledCount;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public IRFunction copy() {
        IRFunction function2 = new IRFunction();
        Map<Object, Object> renameMap = new HashMap<>();
        for (BasicBlock basicBlock: getReversePostOrder()) {
            renameMap.put(basicBlock, new BasicBlock(function2, basicBlock.getName()));
        }
        for (BasicBlock basicBlock: getReversePostOrder()){
            BasicBlock basicBlock2 = (BasicBlock) renameMap.get(basicBlock);
            for (IRInstruction instruction = basicBlock.getFirstInstruction(); instruction != null; instruction = instruction.getNextInstruction()){
                if (instruction instanceof JumpInstruction) {
                    basicBlock2.setJumpInstruction((JumpInstruction) instruction.copyRename(renameMap));
                } else {
                    basicBlock2.addInstruction(instruction.copyRename(renameMap));
                }
            }
        }
        function2.setStartBasicBlock((BasicBlock) renameMap.get(startBasicBlock));
        function2.setEndBasicBlock((BasicBlock) renameMap.get(endBasicBlock));
        function2.setParameters(parameters);
        return function2;
    }

    public Map<ConstantData, VirtualRegister> getConstantDataMap() {
        return constantDataMap;
    }

    public Set<ConstantData> getDefinedConstantData() {
        return definedConstantData;
    }

    public Set<ConstantData> getRecursiveConstantData() {
        return recursiveConstantData;
    }

    public Set<ConstantData> getRecursiveDefinedConstantData() {
        return recursiveDefinedConstantData;
    }

    public Set<ForHyperBlock> getForHyperBlockSet() {
        return forHyperBlockSet;
    }

    public List<PhysicalRegister> getCalleeRegisterList() {
        return calleeRegisterList;
    }

    public List<PhysicalRegister> getCallerRegisterList() {
        return callerRegisterList;
    }

    public Set<PhysicalRegister> getRecursiveRegisterSet() {
        return recursiveRegisterSet;
    }

    public int getStackSlotNumber() {
        return stackSlotNumber;
    }

    public void addStackSlotNumber(int n) {
        stackSlotNumber += n;
    }


    public void setStackSlotNumber(int stackSlotNumber) {
        this.stackSlotNumber = stackSlotNumber;
    }

    public Map<StackSlot, Integer> getStackSlotOffsetMap() {
        return stackSlotOffsetMap;
    }

    public int getExtraParameterNumber() {
        return extraParameterNumber;
    }

    public void setExtraParameterNumber(int extraParameterNumber) {
        this.extraParameterNumber = extraParameterNumber;
    }

    public void parameterTransform() {
        for (int i = 6; i < parameters.size(); ++i) {
            VirtualRegister virtualRegister = parameters.get(i);
            StackSlot stackSlot = new StackSlot(this, "arg" + i, true);
            stackSlotMap.put(virtualRegister, stackSlot);
            startBasicBlock.getFirstInstruction().addFirst(new LoadInstruction(startBasicBlock, virtualRegister, Configuration.getRegisterSize(), stackSlot, 0));
        }
        if (parameters.size() > 0) parameters.get(0).setPhysicalRegister(NASMRegisterSet.rdi);
        if (parameters.size() > 1) parameters.get(1).setPhysicalRegister(NASMRegisterSet.rsi);
        if (parameters.size() > 2) parameters.get(2).setPhysicalRegister(NASMRegisterSet.rdx);
        if (parameters.size() > 3) parameters.get(3).setPhysicalRegister(NASMRegisterSet.rcx);
        if (parameters.size() > 4) parameters.get(4).setPhysicalRegister(NASMRegisterSet.r8);
        if (parameters.size() > 5) parameters.get(5).setPhysicalRegister(NASMRegisterSet.r9);
    }

    public void livenessAnalysis() {
        boolean flag = true;
        Set<VirtualRegister> liveIn = new HashSet<>();
        Set<VirtualRegister> liveOut = new HashSet<>();
        while (flag) {
            flag = false;
            for (BasicBlock basicBlock: getReversePreOrder()) {
                for (IRInstruction instruction = basicBlock.getLastInstruction(); instruction != null; instruction = instruction.getPreviousInstruction()) {
                    liveIn.clear();
                    liveOut.clear();
                    if (instruction instanceof JumpInstruction) {
                        if (instruction instanceof GotoInstruction) {
                            liveOut.addAll(((GotoInstruction) instruction).getBasicBlock().getFirstInstruction().getLiveIn());
                        } else if (instruction instanceof BranchInstruction) {
                            liveOut.addAll(((BranchInstruction) instruction).getThenBasicBlock().getFirstInstruction().getLiveIn());
                            liveOut.addAll(((BranchInstruction) instruction).getElseBasicBlock().getFirstInstruction().getLiveIn());
                        }
                    } else if (instruction.getNextInstruction() != null) {
                        liveOut.addAll(instruction.getNextInstruction().getLiveIn());
                    }
                    liveIn.addAll(liveOut);
                    Register definedRegister = instruction.getDefinedRegister();
                    if (definedRegister instanceof VirtualRegister) {
                        liveIn.remove(definedRegister);
                    }
                    for (Register register : instruction.getRegisterList()) {
                        if (register instanceof VirtualRegister) {
                            liveIn.add((VirtualRegister) register);
                        }
                    }
                    if (!instruction.getLiveIn().equals(liveIn)) {
                        flag = true;
                        instruction.getLiveIn().clear();
                        instruction.getLiveIn().addAll(liveIn);
                    }
                    if (!instruction.getLiveOut().equals(liveOut)) {
                        flag = true;
                        instruction.getLiveOut().clear();
                        instruction.getLiveOut().addAll(liveOut);
                    }
                }
            }
        }
    }

    public boolean eliminate() {
        boolean flag = false;
        for (BasicBlock basicBlock: getReversePreOrder()) {
            for (IRInstruction instruction = basicBlock.getLastInstruction(), previousInstruction; instruction != null; instruction = previousInstruction) {
                previousInstruction = instruction.getPreviousInstruction();
                if (instruction instanceof BinaryOperationInstruction || instruction instanceof UnaryOperationInstruction ||
                    instruction instanceof ComparisonInstruction || instruction instanceof HeapAllocateInstruction ||
                    instruction instanceof LoadInstruction || instruction instanceof MoveInstruction) {
                    if (instruction.getDefinedRegister() == null || !instruction.getLiveOut().contains(instruction.getDefinedRegister())) {
                        flag = true;
                        instruction.remove();
                    }
                }
            }
        }
        for (ForHyperBlock forHyperBlock: forHyperBlockSet) {
            boolean forFlag = false;
            if (forHyperBlock.getBlockBasicBlock() == null || forHyperBlock.getConditionBasicBlock() == null ||
                forHyperBlock.getStepBasicBlock() == null || forHyperBlock.getNextBasicBlock() == null) continue;
            for (BasicBlock basicBlock: forHyperBlock.getBasicBlockList()) {
                for (IRInstruction instruction = basicBlock.getFirstInstruction(); instruction != null; instruction = instruction.getNextInstruction()) {
                    if (instruction instanceof FunctionCallInstruction) {
                        forFlag = true;
                    } else if (instruction.getDefinedRegister() != null) {
                        forFlag = forHyperBlock.getNextBasicBlock().getFirstInstruction().getLiveIn().contains(instruction.getDefinedRegister());
                    } else if (instruction instanceof StoreInstruction || instruction instanceof ReturnInstruction || instruction instanceof PushInstruction) {
                        forFlag = true;
                    } else if (instruction instanceof GotoInstruction) {
                        forFlag = !forHyperBlock.getBasicBlockList().contains(((GotoInstruction) instruction).getBasicBlock());
                    } else if (instruction instanceof BranchInstruction) {
                        forFlag = !forHyperBlock.getBasicBlockList().contains(((BranchInstruction) instruction).getThenBasicBlock()) ||
                                !forHyperBlock.getBasicBlockList().contains(((BranchInstruction) instruction).getElseBasicBlock());
                    }
                    if (forFlag) break;
                }
            }
            if (!forFlag) {
                forHyperBlock.getConditionBasicBlock().reConstruct();
                forHyperBlock.getConditionBasicBlock().setJumpInstruction(new GotoInstruction(forHyperBlock.getConditionBasicBlock(), forHyperBlock.getNextBasicBlock()));
            }
        }
        return flag;
    }

    private Map<BasicBlock, BasicBlock> jumpMap = new HashMap<>();

    BasicBlock getFinalBasicBlock(BasicBlock basicBlock) {
        BasicBlock oldBasicBlock = basicBlock, newBasicBlock = jumpMap.get(basicBlock);
        while (newBasicBlock != null) {
            oldBasicBlock = newBasicBlock;
            newBasicBlock = jumpMap.get(newBasicBlock);
        }
        return oldBasicBlock;
    }

    public void removeBlankBasicBlock() {
        jumpMap.clear();
        for (BasicBlock basicBlock: getReversePostOrder()) {
            if (basicBlock.getFirstInstruction() == basicBlock.getLastInstruction()) {
                if (basicBlock.getFirstInstruction() instanceof GotoInstruction) {
                    jumpMap.put(basicBlock, ((GotoInstruction) basicBlock.getFirstInstruction()).getBasicBlock());
                }
            }
        }
        for (BasicBlock basicBlock: getReversePostOrder()) {
            if (basicBlock.getLastInstruction() instanceof GotoInstruction) {
                GotoInstruction gotoInstruction = (GotoInstruction) basicBlock.getLastInstruction();
                gotoInstruction.setBasicBlock(getFinalBasicBlock(gotoInstruction.getBasicBlock()));
            } else if (basicBlock.getLastInstruction() instanceof BranchInstruction) {
                BranchInstruction branchInstruction = (BranchInstruction) basicBlock.getLastInstruction();
                branchInstruction.setThenBasicBlock(getFinalBasicBlock(branchInstruction.getThenBasicBlock()));
                branchInstruction.setElseBasicBlock(getFinalBasicBlock(branchInstruction.getElseBasicBlock()));
                if (branchInstruction.getThenBasicBlock() == branchInstruction.getElseBasicBlock()) {
                    branchInstruction.replace(new GotoInstruction(basicBlock, branchInstruction.getThenBasicBlock()));
                }
            }
        }
    }

    public void simplifyInstruction() {
        for (BasicBlock basicBlock: reversePostOrder) {
            basicBlock.simplifyInstruction();
        }
    }
}
