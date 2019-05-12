package cn.cyx666.Mxstar.IR;

import cn.cyx666.Mxstar.Configuration.Configuration;
import cn.cyx666.Mxstar.IR.BasicBlock.BasicBlock;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.Function.*;
import cn.cyx666.Mxstar.IR.HyperBlock.*;
import cn.cyx666.Mxstar.IR.Instruction.*;
import cn.cyx666.Mxstar.NASM.*;

import java.util.*;

import static cn.cyx666.Mxstar.Scope.Scope.ARRAY_CLASS_NAME;
import static cn.cyx666.Mxstar.Scope.Scope.STRING_CLASS_NAME;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class IR{
    private Map<String, IRFunction> functionMap = new HashMap<>();
    private Map<String, IRFunction> builtInFunctionMap = new HashMap<>();
    private List<ConstantData> constantDataList = new ArrayList<>();
    private Map<String, ConstantString> constantStringMap = new HashMap<>();
    private PhysicalRegister register0, register1;
    private boolean isDivision = false, isShift = false;
    private Map<BasicBlock, ForHyperBlock> forHyperBlockMap = new HashMap<>();
    private Map<IRFunction, IRFunction> functionBackUpMap = new HashMap<>();
    private int maxParameter = 3;

    public IR(){
        IRFunction function;

        function = new IRFunction(STRING_CONCATENATE_NAME, "__builtin_string_concat");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(STRING_EQUAL_NAME, "__builtin_string_equal");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(STRING_INEQUAL_NAME, "__builtin_string_inequal");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(STRING_LESS_NAME, "__builtin_string_less");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(STRING_LESS_EQUAL_NAME, "__builtin_string_less_equal");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(PRINT_NAME, "_Z5printPc");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(PRINTLN_NAME, "_Z7printlnPc");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);


        function = new IRFunction(PRINTINT_NAME, "_Z8printInti");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(PRINTLNINT_NAME, "_Z10printlnInti");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(GETSTRING_NAME, "_Z9getStringv");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(GETINT_NAME, "_Z6getIntv");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(TOSTRING_NAME, "_Z8toStringi");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(STRING_SUBSTRING_NAME, "_Z27__member___string_substringPcii");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(STRING_PARSEINT_NAME, "_Z26__member___string_parseIntPc");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);

        function = new IRFunction(STRING_ORD_NAME, "_Z21__member___string_ordPci");
        function.getRegisterSet().addAll(NASMRegisterSet.generalRegister);
        addBuiltInFunction(function);
    }

    public void addBuiltInFunction(IRFunction function) {
        builtInFunctionMap.put(function.getName(), function);
    }

    public void addFunction(IRFunction function) {
        functionMap.put(function.getName(), function);
    }

    public void addConstantData(ConstantData data) {
        constantDataList.add(data);
    }

    public PhysicalRegister getRegister0() {
        return register0;
    }

    public PhysicalRegister getRegister1() {
        return register1;
    }

    public Map<String, IRFunction> getFunctionMap() {
        return functionMap;
    }

    public IRFunction getFunction(String name) {
        return functionMap.get(name);
    }

    public IRFunction getBuiltInFunction(String name) {
        return builtInFunctionMap.get(name);
    }

    public void addConstantString(ConstantString string) {
        constantStringMap.put(string.getString(), string);
    }

    public ConstantString getConstantString(String name) {
        return constantStringMap.get(name);
    }

    public List<ConstantData> getConstantDataList() {
        return constantDataList;
    }

    public Map<String, ConstantString> getConstantStringMap() {
        return constantStringMap;
    }

    public void setDivision(boolean division) {
        isDivision = division;
    }

    public void setShift(boolean shift) {
        isShift = shift;
    }

    public void updateCalleeSet() {
        Set<IRFunction> recursiveCalleeSet = new HashSet<>();
        for (IRFunction function : functionMap.values()) {
            function.getRecursiveCalleeSet().clear();
        }
        boolean flag = true;
        while (flag) {
            flag = false;
            for (IRFunction function : functionMap.values()) {
                recursiveCalleeSet.clear();
                recursiveCalleeSet.addAll(function.getCalleeSet());
                for (IRFunction calleeFunction : function.getCalleeSet()) {
                    recursiveCalleeSet.addAll(calleeFunction.getRecursiveCalleeSet());
                }
                if (!recursiveCalleeSet.equals(function.getRecursiveCalleeSet())) {
                    function.getRecursiveCalleeSet().clear();
                    function.getRecursiveCalleeSet().addAll(recursiveCalleeSet);
                    flag = true;
                }
            }
        }
        for (IRFunction function: functionMap.values()) {
            function.setRecursive(function.getRecursiveCalleeSet().contains(function));
        }
    }

    public void addForHyperBlock(ForHyperBlock forHyperBlock) {
        forHyperBlockMap.put(forHyperBlock.getConditionBasicBlock(), forHyperBlock);
        forHyperBlockMap.put(forHyperBlock.getStepBasicBlock(), forHyperBlock);
        forHyperBlockMap.put(forHyperBlock.getBlockBasicBlock(), forHyperBlock);
        forHyperBlockMap.put(forHyperBlock.getNextBasicBlock(), forHyperBlock);
    }

    public ForHyperBlock getForHyperBlock(BasicBlock keyBasicBlock) {
        return forHyperBlockMap.get(keyBasicBlock);
    }

    public Map<BasicBlock, ForHyperBlock> getForHyperBlockMap() {
        return forHyperBlockMap;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    static public final String STRING_CONCATENATE_NAME = STRING_CLASS_NAME + "concat";
    static public final String STRING_EQUAL_NAME = STRING_CLASS_NAME + "equal";
    static public final String STRING_INEQUAL_NAME = STRING_CLASS_NAME + "inequal";
    static public final String STRING_LESS_NAME = STRING_CLASS_NAME + "less";
    static public final String STRING_LESS_EQUAL_NAME = STRING_CLASS_NAME + "less_equal";

    static public final String PRINT_NAME = "print";
    static public final String PRINTLN_NAME = "println";
    static public final String PRINTINT_NAME = "printInt";
    static public final String PRINTLNINT_NAME = "printlnInt";
    static public final String GETSTRING_NAME = "getString";
    static public final String GETINT_NAME = "getInt";
    static public final String TOSTRING_NAME = "toString";
    static public final String STRING_LENGTH_NAME = STRING_CLASS_NAME + "_length";
    static public final String STRING_SUBSTRING_NAME = STRING_CLASS_NAME + "_substring";
    static public final String STRING_PARSEINT_NAME = STRING_CLASS_NAME + "_parseInt";
    static public final String STRING_ORD_NAME = STRING_CLASS_NAME + "_ord";
    static public final String ARRAY_SIZE_NAME = ARRAY_CLASS_NAME + "_size";

    public void toTwoOperands() {
        for (IRFunction function: functionMap.values()) {
            for (BasicBlock basicBlock: function.getReversePostOrder()) {
                for (IRInstruction irInstruction = basicBlock.getFirstInstruction(); irInstruction != null; irInstruction = irInstruction.getNextInstruction()) {
                    if (!(irInstruction instanceof BinaryOperationInstruction)) {
                        continue;
                    }
                    BinaryOperationInstruction instruction = (BinaryOperationInstruction) irInstruction;
                    if (instruction.getDestination() == instruction.getLeftData()) {
                        continue;
                    }
                    if (instruction.getDestination() == instruction.getRightData()) {
                        if (instruction.isCommutative()) {
                            instruction.setRightData(instruction.getLeftData());
                            instruction.setLeftData(instruction.getDestination());
                        } else {
                            VirtualRegister virtualRegister = new VirtualRegister("rightData");
                            instruction.addFirst(new MoveInstruction(instruction.getParentBasicBlock(), virtualRegister, instruction.getRightData()));
                            instruction.addFirst(new MoveInstruction(instruction.getParentBasicBlock(), instruction.getDestination(), instruction.getLeftData()));
                            instruction.setLeftData(instruction.getDestination());
                            instruction.setRightData(virtualRegister);
                        }
                    } else if (instruction.getOperator() != BinaryOperationInstruction.BinaryOperator.DIV && instruction.getOperator() != BinaryOperationInstruction.BinaryOperator.MOD) {
                        instruction.addFirst(new MoveInstruction(instruction.getParentBasicBlock(), instruction.getDestination(), instruction.getLeftData()));
                        instruction.setLeftData(instruction.getDestination());
                    }
                }
            }
        }
    }



    public void inlineFunction(){
        final int MAX_INLINE_INSTRUCTION = 32;
        final int MAX_CALLEE_INSTRUCTION = 64;
        final int MAX_FUNCTION_INSTRUCTION = 65536;
        final int MAX_INLINE_DEPTH = 5;
        List<BasicBlock> reversePostOrder = new ArrayList<>();
        List<String> unCalledFunction = new ArrayList<>();
        boolean flag = true;
        while (flag) {
            flag = false;
            unCalledFunction.clear();
            for (IRFunction function: functionMap.values()){
                boolean localFlag = false;
                reversePostOrder.clear();
                reversePostOrder.addAll(function.getReversePostOrder());
                for (BasicBlock basicBlock : reversePostOrder) {
                    for (IRInstruction instruction = basicBlock.getFirstInstruction(), nextInstruction; instruction != null; instruction = nextInstruction) {
                        IRFunction callee;
                        nextInstruction = instruction.getNextInstruction();
                        if (!(instruction instanceof FunctionCallInstruction)) continue;
                        callee = ((FunctionCallInstruction) instruction).getFunction();
                        if (callee.isBuiltIn()) continue;
                        if (callee.isRecursive()) continue;
                        if (callee.getEntity().isMember()) continue;
                        if (callee.getInstructionCount() > MAX_CALLEE_INSTRUCTION || callee.getInstructionCount() + function.getInstructionCount() > MAX_FUNCTION_INSTRUCTION) continue;
                        nextInstruction = inlineFunctionCall((FunctionCallInstruction) instruction);
                        function.addInstructionCount(callee.getInstructionCount());
                        flag = true;
                        localFlag = true;
                        //callee.addCalledCounted(-1);
                        if (callee.getCalledCount() == 0) {
                            unCalledFunction.add(callee.getName());
                        }
                    }
                }
                if (localFlag) {
                    function.updateReversePostOrder();
                }
            }
            for (String function: unCalledFunction) {
                functionMap.remove(function);
            }
        }
        for (IRFunction function: functionMap.values()) {
            function.updateCalleeSet();
        }
        updateCalleeSet();
        reversePostOrder = new ArrayList<>();
        flag = true;
        for (int i = 0; flag && i < MAX_INLINE_DEPTH; ++i){
            flag = false;
            functionBackUpMap.clear();
            for (IRFunction function : functionMap.values()) {
                if (!function.isRecursive()) continue;
                functionBackUpMap.put(function, function.copy());
            }
            for (IRFunction function : functionMap.values()) {
                boolean localFlag = false;
                reversePostOrder.clear();
                reversePostOrder.addAll(function.getReversePostOrder());
                for (BasicBlock basicBlock: reversePostOrder) {
                    for (IRInstruction instruction = basicBlock.getFirstInstruction(), nextInstruction; instruction != null; instruction = nextInstruction) {
                        IRFunction callee;
                        nextInstruction = instruction.getNextInstruction();
                        if (!(instruction instanceof FunctionCallInstruction)) continue;
                        callee = ((FunctionCallInstruction) instruction).getFunction();
                        if (callee.isBuiltIn()) continue;
                        if (callee.getEntity().isMember()) continue;
                        if (callee.getInstructionCount() > MAX_INLINE_INSTRUCTION || callee.getInstructionCount() + function.getInstructionCount() > MAX_FUNCTION_INSTRUCTION) continue;
                        nextInstruction = inlineFunctionCall((FunctionCallInstruction) instruction);
                        function.addInstructionCount(callee.getInstructionCount());
                        flag = true;
                        localFlag = true;
                    }
                }
                if (localFlag) {
                    function.updateReversePostOrder();
                }
            }
        }
        for (IRFunction function: functionMap.values()) {
            function.updateCalleeSet();
        }
        updateCalleeSet();
    }

    private IRInstruction inlineFunctionCall(FunctionCallInstruction functionCallInstruction) {
        IRFunction caller = functionCallInstruction.getParentBasicBlock().getFunction(), callee = functionBackUpMap.getOrDefault(functionCallInstruction.getFunction(), functionCallInstruction.getFunction());
        List<BasicBlock> reversePostOrder = callee.getReversePostOrder();
        Map<Object, Object> renameMap = new HashMap<>();
        BasicBlock oldEndBasicBlock = callee.getEndBasicBlock();
        BasicBlock newEndBasicBlock = new BasicBlock(caller, oldEndBasicBlock.getName());
        renameMap.put(oldEndBasicBlock, newEndBasicBlock);
        renameMap.put(callee.getStartBasicBlock(), functionCallInstruction.getParentBasicBlock());
        if (caller.getEndBasicBlock() == functionCallInstruction.getParentBasicBlock()) {
            caller.setEndBasicBlock(newEndBasicBlock);
        }
        Map<Object, Object> callRenameMap = Collections.singletonMap(functionCallInstruction.getParentBasicBlock(), newEndBasicBlock);
        for (IRInstruction instruction = functionCallInstruction.getNextInstruction(); instruction != null; instruction = instruction.getNextInstruction()) {
            if (instruction instanceof JumpInstruction) {
                newEndBasicBlock.setJumpInstruction((JumpInstruction) instruction.copyRename(callRenameMap));
            } else {
                newEndBasicBlock.addInstruction(instruction.copyRename(callRenameMap));
            }
            instruction.remove();
        }
        IRInstruction newFirstInstruction = newEndBasicBlock.getFirstInstruction();
        for (int i = 0; i < functionCallInstruction.getParameters().size(); ++i) {
            VirtualRegister oldParameter = callee.getParameters().get(i);
            VirtualRegister newParameter = oldParameter.copy();
            functionCallInstruction.addFirst(new MoveInstruction(functionCallInstruction.getParentBasicBlock(), newParameter, functionCallInstruction.getParameters().get(i)));
            renameMap.put(oldParameter, newParameter);
        }
        functionCallInstruction.remove();
        for (BasicBlock basicBlock : reversePostOrder) {
            if (!renameMap.containsKey(basicBlock)) {
                renameMap.put(basicBlock, new BasicBlock(caller, basicBlock.getName()));
            }
        }
        for (BasicBlock oldBasicBlock : reversePostOrder) {
            BasicBlock newBasicBlock = (BasicBlock) renameMap.get(oldBasicBlock);
            if (forHyperBlockMap.containsKey(oldBasicBlock)) {
                ForHyperBlock forHyperBlock = forHyperBlockMap.get(oldBasicBlock);
                forHyperBlock.replace(oldBasicBlock, newBasicBlock);
                forHyperBlockMap.remove(oldBasicBlock);
                forHyperBlockMap.put(newBasicBlock, forHyperBlock);
            }

            for (IRInstruction instruction = oldBasicBlock.getFirstInstruction(); instruction != null; instruction = instruction.getNextInstruction()) {
                for (Data data : instruction.getDataList()) {
                    if (!renameMap.containsKey(data)) renameMap.put(data, data.copy());
                }
                if (instruction.getDefinedRegister() != null) {
                    if (!renameMap.containsKey(instruction.getDefinedRegister())) renameMap.put(instruction.getDefinedRegister(), instruction.getDefinedRegister().copy());
                }
                if (newBasicBlock == newEndBasicBlock) {
                    if (!(instruction instanceof ReturnInstruction)) {
                        newFirstInstruction.addFirst(instruction.copyRename(renameMap));
                    }
                } else {
                    if (instruction instanceof JumpInstruction) {
                        if (!(instruction instanceof ReturnInstruction)) {
                            newBasicBlock.setJumpInstruction((JumpInstruction) instruction.copyRename(renameMap));
                        }
                    } else {
                        newBasicBlock.addInstruction(instruction.copyRename(renameMap));
                    }
                }
            }
        }
        if (!functionCallInstruction.getParentBasicBlock().isJump()) {
            functionCallInstruction.getParentBasicBlock().setJumpInstruction(new GotoInstruction(functionCallInstruction.getParentBasicBlock(), newEndBasicBlock));
        }
        ReturnInstruction returnInstruction = callee.getReturnList().get(0);
        if (returnInstruction.getData() != null) {
            newFirstInstruction.addFirst(new MoveInstruction(newEndBasicBlock, functionCallInstruction.getDestination(), (Data) renameMap.get(returnInstruction.getData())));
        }
        return newEndBasicBlock.getFirstInstruction();
    }

    public void constantData() {
        for (IRFunction function: functionMap.values()) {
            Map<Register, Register> renameMap = new HashMap<>();
            for (BasicBlock basicBlock: function.getReversePostOrder()) {
                for (IRInstruction instruction = basicBlock.getFirstInstruction(); instruction != null; instruction = instruction.getNextInstruction()){
                    if ((instruction instanceof LoadInstruction && ((LoadInstruction) instruction).isConstant()) ||
                            (instruction instanceof StoreInstruction && ((StoreInstruction) instruction).isConstant())) continue;
                    List<Register> registerList = instruction.getRegisterList();
                    if (!registerList.isEmpty()) {
                        renameMap.clear();
                        for (Register register : registerList) {
                            if (register instanceof ConstantData && !(register instanceof ConstantString)) {
                                VirtualRegister virtualRegister = function.getConstantDataMap().get(register);
                                if (virtualRegister == null) {
                                    virtualRegister = new VirtualRegister(((ConstantData) register).getName());
                                    function.getConstantDataMap().put((ConstantData) register, virtualRegister);
                                }
                                renameMap.put(register, virtualRegister);
                            } else {
                                renameMap.put(register, register);
                            }
                        }
                        instruction.setRegister(renameMap);
                    }
                    Register definedRegister = instruction.getDefinedRegister();
                    if (definedRegister instanceof ConstantData) {
                        VirtualRegister virtualRegister = function.getConstantDataMap().get(definedRegister);
                        if (virtualRegister == null) {
                            virtualRegister = new VirtualRegister(((ConstantData) definedRegister).getName());
                            function.getConstantDataMap().put((ConstantData) definedRegister, virtualRegister);
                        }
                        instruction.setDefinedRegister(virtualRegister);
                        function.getDefinedConstantData().add((ConstantData) definedRegister);
                    }
                }
            }
            BasicBlock startBasicBlock = function.getStartBasicBlock();
            IRInstruction firstInstruction = startBasicBlock.getFirstInstruction();
            for (ConstantData constantData: function.getConstantDataMap().keySet()) {
                firstInstruction.addFirst(new LoadInstruction(startBasicBlock, function.getConstantDataMap().get(constantData), Configuration.getRegisterSize(), constantData, constantData instanceof ConstantString));
            }
        }

        for (IRFunction function : functionMap.values()) {
            function.getRecursiveConstantData().addAll(function.getConstantDataMap().keySet());
            function.getRecursiveDefinedConstantData().addAll(function.getDefinedConstantData());
            for (IRFunction callee : function.getRecursiveCalleeSet()) {
                function.getRecursiveConstantData().addAll(callee.getConstantDataMap().keySet());
                function.getRecursiveDefinedConstantData().addAll(callee.getDefinedConstantData());
            }
        }

        for (IRFunction function: functionMap.values()) {
            Set<ConstantData> constantDataSet = function.getConstantDataMap().keySet();
            if (constantDataSet.isEmpty()) continue;
            for (BasicBlock basicBlock: function.getReversePostOrder()) {
                for (IRInstruction instruction = basicBlock.getFirstInstruction(); instruction != null; instruction = instruction.getNextInstruction()) {
                    if (!(instruction instanceof FunctionCallInstruction)) continue;
                    IRFunction callee = ((FunctionCallInstruction) instruction).getFunction();
                    for (ConstantData constantData: function.getDefinedConstantData()) {
                        if (constantData instanceof ConstantString) continue;
                        if (callee.getRecursiveConstantData().contains(constantData)) {
                            instruction.addFirst(new StoreInstruction(basicBlock, function.getConstantDataMap().get(constantData), Configuration.getRegisterSize(), constantData));
                        }
                    }
                    if (callee.getRecursiveDefinedConstantData().isEmpty()) continue;
                    Set<ConstantData> loadConstantDataSet = new HashSet<>();
                    loadConstantDataSet.addAll(callee.getRecursiveDefinedConstantData());
                    loadConstantDataSet.retainAll(constantDataSet);
                    for (ConstantData constantData : loadConstantDataSet) {
                        if (constantData instanceof ConstantString) continue;
                        instruction.addLast(new LoadInstruction(basicBlock, function.getConstantDataMap().get(constantData), Configuration.getRegisterSize(), constantData, false));
                    }
                }
            }
        }
        for (IRFunction function : functionMap.values()) {
            ReturnInstruction instruction = function.getReturnList().get(0);
            for (ConstantData constantData : function.getDefinedConstantData()) {
                instruction.addFirst(new StoreInstruction(instruction.getParentBasicBlock(), function.getConstantDataMap().get(constantData), Configuration.getRegisterSize(), constantData));
            }
        }
    }

    public void parameterTransform() {
        for (IRFunction function: functionMap.values()) {
            function.parameterTransform();
        }
    }

    public void livenessAnalysis() {
        for (IRFunction function: functionMap.values()) {
            function.livenessAnalysis();
        }
        boolean flag = true;
        while (flag) {
            flag = false;
            for (IRFunction function: functionMap.values()) {
                if (function.isBuiltIn()) continue;
                flag |= function.eliminate();
                function.removeBlankBasicBlock();
                function.livenessAnalysis();
            }
        }
    }

    private List<PhysicalRegister> physicalRegisterList= new ArrayList<>(NASMRegisterSet.generalRegister);
    private int colorNumber;
    private Set<VirtualRegister> virtualRegisterSet = new HashSet<>();
    private List<VirtualRegister> virtualRegisterOrder = new ArrayList<>();
    private Set<PhysicalRegister> colorSet = new HashSet<>();
    private Set<VirtualRegister> nodeSet = new HashSet<>();
    private Set<VirtualRegister> degreeSmallNodeSet = new HashSet<>();


    void removeNode(VirtualRegister register) {
        register.setRemoved(true);
        nodeSet.remove(register);
        for (VirtualRegister neighbor: register.getNeighbor()) {
            if (neighbor.isRemoved()) continue;
            neighbor.addDegree(-1);
            if (neighbor.getDegree() < colorNumber) {
                degreeSmallNodeSet.add(neighbor);
            }
        }
    }

    public void registerAllocate(){
        Map<Register, Register> renameMap = new HashMap<>();
        for (IRFunction function: functionMap.values()) {
            maxParameter = max(maxParameter, function.getParameters().size());
        }
        if (maxParameter >= 5) physicalRegisterList.remove(NASMRegisterSet.r8);
        if (maxParameter >= 6) physicalRegisterList.remove(NASMRegisterSet.r9);
        if (isShift || isDivision) {
            register0 = physicalRegisterList.get(0);
            register1 = physicalRegisterList.get(1);
        } else {
            register0 = NASMRegisterSet.rbx;
            register1 = physicalRegisterList.get(0);
        }
        physicalRegisterList.remove(register0);
        physicalRegisterList.remove(register1);
        colorNumber = physicalRegisterList.size();
        for (IRFunction function: functionMap.values()) {
            for (VirtualRegister virtualRegister: virtualRegisterSet) {
                virtualRegister.resetColor();
            }
            nodeSet.clear();
            degreeSmallNodeSet.clear();
            virtualRegisterSet.addAll(function.getParameters());
            for (BasicBlock basicBlock: function.getReversePreOrder()) {
                for (IRInstruction instruction = basicBlock.getFirstInstruction(); instruction != null; instruction = instruction.getNextInstruction()) {
                    Register definedRegister = instruction.getDefinedRegister();
                    if (!(definedRegister instanceof VirtualRegister)) continue;
                    virtualRegisterSet.add((VirtualRegister) definedRegister);
                    if (instruction instanceof MoveInstruction) {
                        Data data = ((MoveInstruction) instruction).getData();
                        if (data instanceof VirtualRegister) {
                            virtualRegisterSet.add((VirtualRegister) data);
                            ((VirtualRegister) data).getSameRegister().add((VirtualRegister) definedRegister);
                            ((VirtualRegister) definedRegister).getSameRegister().add((VirtualRegister) data);
                        }
                        for (VirtualRegister virtualRegister: instruction.getLiveOut()) {
                            if (virtualRegister != data && virtualRegister != definedRegister) {
                                ((VirtualRegister) definedRegister).getNeighbor().add(virtualRegister);
                                virtualRegister.getNeighbor().add((VirtualRegister) definedRegister);
                            }
                        }
                    } else {
                        for (VirtualRegister virtualRegister: instruction.getLiveOut()) {
                            if (virtualRegister != definedRegister) {
                                ((VirtualRegister) definedRegister).getNeighbor().add(virtualRegister);
                                virtualRegister.getNeighbor().add((VirtualRegister) definedRegister);
                            }
                        }
                    }
                }
            }
            for (VirtualRegister virtualRegister: virtualRegisterSet) {
                virtualRegister.setDegree(virtualRegister.getNeighbor().size());
            }
            nodeSet.addAll(virtualRegisterSet);
            for (VirtualRegister virtualRegister: nodeSet) {
                if (virtualRegister.getDegree() < colorNumber) {
                    degreeSmallNodeSet.add(virtualRegister);
                }
            }
            virtualRegisterOrder.clear();
            while (!nodeSet.isEmpty()) {
                while (!degreeSmallNodeSet.isEmpty()) {
                    Iterator<VirtualRegister> iterator = degreeSmallNodeSet.iterator();
                    VirtualRegister virtualRegister = iterator.next();
                    iterator.remove();
                    removeNode(virtualRegister);
                    virtualRegisterOrder.add(virtualRegister);
                }
                if (nodeSet.isEmpty()) break;
                Iterator<VirtualRegister> iterator = nodeSet.iterator();
                VirtualRegister virtualRegister = iterator.next();
                iterator.remove();
                removeNode(virtualRegister);
                virtualRegisterOrder.add(virtualRegister);
            }
            Collections.reverse(virtualRegisterOrder);
            for (VirtualRegister virtualRegister: virtualRegisterOrder) {
                virtualRegister.setRemoved(false);
                colorSet.clear();
                for (VirtualRegister neighbor: virtualRegister.getNeighbor()) {
                    if (!neighbor.isRemoved() && neighbor.getColor() instanceof PhysicalRegister) {
                        colorSet.add((PhysicalRegister) neighbor.getColor());
                    }
                }
                PhysicalRegister physicalRegister = virtualRegister.getPhysicalRegister();
                if (physicalRegister != null) {
                    virtualRegister.setColor(physicalRegister);
                } else {
                    for (VirtualRegister sameColor: virtualRegister.getSameRegister()) {
                        virtualRegisterSet.add(sameColor);
                        if (sameColor.getColor() instanceof PhysicalRegister && !colorSet.contains(sameColor.getColor())) {
                            virtualRegister.setColor(sameColor.getColor());
                            break;
                        }
                    }
                }
                if (virtualRegister.getColor() == null) {
                    for (PhysicalRegister register: physicalRegisterList) {
                        if (!colorSet.contains(register)) {
                            virtualRegister.setColor(register);
                            break;
                        }
                    }
                }
                if (virtualRegister.getColor() == null) {
                    virtualRegister.setColor(function.getStackSlotMap().get(virtualRegister));
                }
                if (virtualRegister.getColor() == null) {
                    virtualRegister.setColor(new StackSlot(function, virtualRegister.getName(), false));
                }
            }
            for (BasicBlock basicBlock: function.getReversePreOrder()) {
                for (IRInstruction instruction = basicBlock.getFirstInstruction(); instruction != null; instruction = instruction.getNextInstruction()) {
                    if (instruction instanceof FunctionCallInstruction) {
                        List<Data> parameters = ((FunctionCallInstruction) instruction).getParameters();
                        for (int i = 0; i < parameters.size(); ++i) {
                            if (parameters.get(i) instanceof VirtualRegister) {
                                parameters.set(i, ((VirtualRegister) parameters.get(i)).getColor());
                            }
                        }
                    } else {
                            List<Register> registerList = instruction.getRegisterList();
                            if (instruction instanceof HeapAllocateInstruction) {
                                function.getRegisterSet().add(NASMRegisterSet.rdi);
                            }
                            if (!registerList.isEmpty()) {
                                boolean isRegister0 = false;
                                renameMap.clear();
                                for (Register register: registerList) {
                                    if (register instanceof VirtualRegister) {
                                        if (((VirtualRegister) register).getColor() instanceof StackSlot) {
                                            PhysicalRegister physicalRegister = isRegister0? register1 : register0;
                                            isRegister0 = true;
                                            instruction.addFirst(new LoadInstruction(basicBlock, physicalRegister, Configuration.getRegisterSize(), ((VirtualRegister) register).getColor(), 0));
                                            renameMap.put(register, physicalRegister);
                                            function.getRegisterSet().add(physicalRegister);
                                        } else {
                                            renameMap.put(register, ((VirtualRegister) register).getColor());
                                            function.getRegisterSet().add((PhysicalRegister) ((VirtualRegister) register).getColor());
                                        }
                                    } else {
                                        renameMap.put(register, register);
                                    }
                                }
                                instruction.setRegister(renameMap);
                            }
                        }
                    Register definedRegister = instruction.getDefinedRegister();
                    if (definedRegister instanceof VirtualRegister) {
                        if (((VirtualRegister) definedRegister).getColor() instanceof StackSlot) {
                            instruction.setDefinedRegister(register0);
                            instruction.addLast(new StoreInstruction(basicBlock, register0, Configuration.getRegisterSize(), ((VirtualRegister) definedRegister).getColor(), 0));
                            function.getRegisterSet().add(register0);
                            instruction = instruction.getNextInstruction();
                        } else {
                            instruction.setDefinedRegister(((VirtualRegister) definedRegister).getColor());
                            function.getRegisterSet().add((PhysicalRegister) ((VirtualRegister) definedRegister).getColor());
                        }
                    }
                }
            }
        }
    }

    public void toNASM() {
        for (IRFunction function: functionMap.values()) {
            for (int i = 0; i < function.getParameters().size(); ++i) {
                if (function.getParameters().get(i).getColor() instanceof PhysicalRegister) {
                    function.getRegisterSet().add((PhysicalRegister) function.getParameters().get(i).getColor());
                }
            }
        }
        for (IRFunction function: functionMap.values()) {
            for (PhysicalRegister physicalRegister: function.getRegisterSet()) {
                if (physicalRegister.isCalleeSave()) function.getCalleeRegisterList().add(physicalRegister);
                if (physicalRegister.isCallerSave()) function.getCallerRegisterList().add(physicalRegister);
            }
            function.getCalleeRegisterList().add(NASMRegisterSet.rbx);
            function.getCalleeRegisterList().add(NASMRegisterSet.rbp);
            function.setStackSlotNumber(function.getStackSlotList().size());
            for (int i = 0; i < function.getStackSlotNumber(); ++i) {
                function.getStackSlotOffsetMap().put(function.getStackSlotList().get(i), i * Configuration.getRegisterSize());
            }
            function.addStackSlotNumber(((function.getCalleeRegisterList().size() + function.getStackSlotNumber()) & 1) ^ 1);
            function.setExtraParameterNumber(max(function.getParameters().size() - 6, 0));
            int extraOffset = (function.getCalleeRegisterList().size() + function.getStackSlotNumber() + 1) * Configuration.getRegisterSize();
            for (int i = 6; i < function.getParameters().size(); ++i) {
                function.getStackSlotOffsetMap().put(function.getStackSlotMap().get(function.getParameters().get(i)), extraOffset);
                extraOffset += Configuration.getRegisterSize();
            }
        }
        for (IRFunction function: functionMap.values()) {
            function.getRecursiveRegisterSet().addAll(function.getRegisterSet());
            for (IRFunction callee: function.getRecursiveCalleeSet()) {
                function.getRecursiveRegisterSet().addAll(callee.getRegisterSet());
            }
        }
        for (IRFunction function: builtInFunctionMap.values()) {
            function.getRecursiveRegisterSet().addAll(function.getRegisterSet());
            for (IRFunction callee: function.getRecursiveCalleeSet()) {
                function.getRecursiveRegisterSet().addAll(callee.getRegisterSet());
            }
        }
        for (IRFunction function: functionMap.values()) {
            BasicBlock startBasicBlock = function.getStartBasicBlock();
            IRInstruction firstInstruction = startBasicBlock.getFirstInstruction();
            for (PhysicalRegister physicalRegister: function.getCalleeRegisterList()){
                firstInstruction.addFirst(new PushInstruction(startBasicBlock, physicalRegister));
            }
            if (function.getStackSlotNumber() > 0) {
                firstInstruction.addFirst(new BinaryOperationInstruction(startBasicBlock, NASMRegisterSet.rsp, BinaryOperationInstruction.BinaryOperator.SUB, NASMRegisterSet.rsp, new ConstantInt(function.getStackSlotNumber() * Configuration.getRegisterSize())));
            }
            firstInstruction.addFirst(new MoveInstruction(startBasicBlock, NASMRegisterSet.rbp, NASMRegisterSet.rsp));
            for (BasicBlock basicBlock: function.getReversePostOrder()) {
                for (IRInstruction instruction = basicBlock.getFirstInstruction(); instruction != null; instruction = instruction.getNextInstruction()) {
                    if (instruction instanceof FunctionCallInstruction) {
                        IRFunction callee = ((FunctionCallInstruction) instruction).getFunction();
                        int pushCallerNumber = 0;
                        for (PhysicalRegister physicalRegister: function.getCallerRegisterList()) {
                            if (physicalRegister.isArg6() && physicalRegister.getArg6Index() < function.getParameters().size()) continue;
                            if (callee.getRecursiveRegisterSet().contains(physicalRegister)) {
                                ++pushCallerNumber;
                                instruction.addFirst(new PushInstruction(instruction.getParentBasicBlock(), physicalRegister));
                            }
                        }
                        int pushArg6Number = min(function.getParameters().size(), 6);
                        for (int i = 0; i < pushArg6Number; ++i) {
                            instruction.addFirst(new PushInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.arg6.get(i)));
                        }
                        pushCallerNumber += pushArg6Number;
                        boolean extraPush = false;
                        List<Data> parameterList = ((FunctionCallInstruction) instruction).getParameters();
                        List<Integer> arg6OffsetList = new ArrayList<>();
                        Map<PhysicalRegister, Integer> arg6OffsetMap = new HashMap<>();
                        if (((pushCallerNumber + callee.getExtraParameterNumber()) & 1) == 1) {
                            extraPush = true;
                            instruction.addFirst(new PushInstruction(instruction.getParentBasicBlock(), new ConstantInt(0)));
                        }
                        for (int i = parameterList.size() - 1; i > 5; --i) {
                            if (parameterList.get(i) instanceof StackSlot) {
                                instruction.addFirst(new LoadInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.rax, Configuration.getRegisterSize(), NASMRegisterSet.rbp, function.getStackSlotOffsetMap().get(parameterList.get(i))));
                                instruction.addFirst(new PushInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.rax));
                            } else {
                                instruction.addFirst(new PushInstruction(instruction.getParentBasicBlock(), parameterList.get(i)));
                            }
                        }
                        int offset = 0;
                        for (int i = 0; i < 6; ++i) {
                            if (parameterList.size() <= i) break;
                            if (parameterList.get(i) instanceof PhysicalRegister &&
                                    ((PhysicalRegister) parameterList.get(i)).isArg6() &&
                                    ((PhysicalRegister) parameterList.get(i)).getArg6Index() < parameterList.size()) {
                                PhysicalRegister physicalRegister = (PhysicalRegister) parameterList.get(i);
                                if (arg6OffsetMap.containsKey(physicalRegister)) {
                                    arg6OffsetList.add(arg6OffsetMap.get(physicalRegister));
                                } else {
                                    arg6OffsetList.add(offset);
                                    arg6OffsetMap.put(physicalRegister, offset);
                                    instruction.addFirst(new PushInstruction(instruction.getParentBasicBlock(), physicalRegister));
                                    ++offset;
                                }
                            } else {
                                arg6OffsetList.add(-1);
                            }
                        }
                        for (int i = 0; i < 6; ++i) {
                            if (parameterList.size() <= i) break;
                            if (arg6OffsetList.get(i) == -1) {
                                if (parameterList.get(i) instanceof StackSlot) {
                                    instruction.addFirst(new LoadInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.rax, Configuration.getRegisterSize(), NASMRegisterSet.rbp, function.getStackSlotOffsetMap().get(parameterList.get(i))));
                                    instruction.addFirst(new MoveInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.arg6.get(i), NASMRegisterSet.rax));
                                } else {
                                    instruction.addFirst(new MoveInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.arg6.get(i), parameterList.get(i)));
                                }
                            } else {
                                instruction.addFirst(new LoadInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.arg6.get(i), Configuration.getRegisterSize(), NASMRegisterSet.rsp, Configuration.getRegisterSize() * (offset - arg6OffsetList.get(i) - 1)));
                            }
                        }
                        if (offset > 0){
                            instruction.addFirst(new BinaryOperationInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.rsp, BinaryOperationInstruction.BinaryOperator.ADD, NASMRegisterSet.rsp, new ConstantInt(offset * Configuration.getRegisterSize())));
                        }
                        if (((FunctionCallInstruction) instruction).getDestination() != null) {
                            instruction.addLast(new MoveInstruction(instruction.getParentBasicBlock(), ((FunctionCallInstruction) instruction).getDestination(), NASMRegisterSet.rax));
                        }
                        for (PhysicalRegister physicalRegister: function.getCallerRegisterList()) {
                            if (physicalRegister.isArg6() && physicalRegister.getArg6Index() < function.getParameters().size()) continue;
                            if (callee.getRecursiveRegisterSet().contains(physicalRegister)) {
                                instruction.addLast(new PopInstruction(instruction.getParentBasicBlock(), physicalRegister));
                            }
                        }
                        for (int i = 0; i < pushArg6Number; ++i) {
                            instruction.addLast(new PopInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.arg6.get(i)));
                        }
                        if (callee.getExtraParameterNumber() > 0 || extraPush) {
                            int pushNumber = extraPush ? callee.getExtraParameterNumber() + 1: callee.getExtraParameterNumber();
                            instruction.addLast(new BinaryOperationInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.rsp, BinaryOperationInstruction.BinaryOperator.ADD, NASMRegisterSet.rsp, new ConstantInt(pushNumber * Configuration.getRegisterSize())));
                        }
                    } else if (instruction instanceof HeapAllocateInstruction) {
                        int pushCallerNumber = 0;
                        for (PhysicalRegister physicalRegister: function.getCallerRegisterList()) {
                            ++pushCallerNumber;
                            instruction.addFirst(new PushInstruction(instruction.getParentBasicBlock(), physicalRegister));
                        }
                        instruction.addFirst(new MoveInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.rdi, ((HeapAllocateInstruction) instruction).getSize()));
                        if ((pushCallerNumber & 1) == 1) {
                            instruction.addFirst(new PushInstruction(instruction.getParentBasicBlock(), new ConstantInt(0)));
                        }
                        instruction.addLast(new MoveInstruction(instruction.getParentBasicBlock(), ((HeapAllocateInstruction) instruction).getDestination(), NASMRegisterSet.rax));
                        for (PhysicalRegister physicalRegister: function.getCallerRegisterList()) {
                            instruction.addLast(new PopInstruction(instruction.getParentBasicBlock(), physicalRegister));
                        }
                        if ((pushCallerNumber & 1) == 1) {
                            instruction.addLast(new BinaryOperationInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.rsp, BinaryOperationInstruction.BinaryOperator.ADD, NASMRegisterSet.rsp, new ConstantInt(Configuration.getRegisterSize())));
                        }
                    } else if (instruction instanceof LoadInstruction) {
                        if (((LoadInstruction) instruction).getAddress() instanceof StackSlot){
                            ((LoadInstruction) instruction).setOffset(function.getStackSlotOffsetMap().get(((LoadInstruction) instruction).getAddress()));
                            ((LoadInstruction) instruction).setAddress(NASMRegisterSet.rbp);
                        }
                    } else if (instruction instanceof StoreInstruction) {
                        if (((StoreInstruction) instruction).getAddress() instanceof StackSlot) {
                            ((StoreInstruction) instruction).setOffset(function.getStackSlotOffsetMap().get(((StoreInstruction) instruction).getAddress()));
                            ((StoreInstruction) instruction).setAddress(NASMRegisterSet.rbp);
                        }
                    } else if (instruction instanceof MoveInstruction) {
                        if (((MoveInstruction) instruction).getDestination() == ((MoveInstruction) instruction).getData()) {
                            instruction.remove();
                        }
                    }
                }
            }
            ReturnInstruction instruction = function.getReturnList().get(0);
            if (instruction.getData() != null) {
                instruction.addFirst(new MoveInstruction(instruction.getParentBasicBlock(), NASMRegisterSet.rax, instruction.getData()));
            }

            BasicBlock endBasicBlock = function.getEndBasicBlock();
            IRInstruction lastInstruction = endBasicBlock.getLastInstruction();
            if (function.getStackSlotNumber() > 0) {
                lastInstruction.addFirst(new BinaryOperationInstruction(endBasicBlock, NASMRegisterSet.rsp, BinaryOperationInstruction.BinaryOperator.ADD, NASMRegisterSet.rsp, new ConstantInt(function.getStackSlotNumber() * Configuration.getRegisterSize())));
            }
            for (int i = function.getCalleeRegisterList().size() - 1; i >= 0; --i) {
                lastInstruction.addFirst(new PopInstruction(endBasicBlock, function.getCalleeRegisterList().get(i)));
            }
        }
    }

    public void simplifyInstruction() {
        for (IRFunction function: functionMap.values()) {
            function.simplifyInstruction();
        }
    }
}
