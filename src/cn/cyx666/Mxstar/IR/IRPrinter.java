package cn.cyx666.Mxstar.IR;

import cn.cyx666.Mxstar.IR.BasicBlock.BasicBlock;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.Function.IRFunction;
import cn.cyx666.Mxstar.IR.Instruction.*;

import java.io.PrintStream;
import java.util.*;

public class IRPrinter implements IRVisitor {
    private PrintStream printStream;
    private Map<BasicBlock, String> basicBlockMap = new HashMap<>();
    private Map<VirtualRegister, String> registerMap = new HashMap<>();
    private Map<ConstantData, String> constantDataMap = new HashMap<>();

    private Map<String, Integer> basicBlockCounter = new HashMap<>();
    private Map<String, Integer> registerCounter = new HashMap<>();
    private Map<String, Integer> constantDataCounter = new HashMap<>();

    private Set<BasicBlock> visitedBasicBlock = new HashSet<>();

    private boolean isConstant;

    public IRPrinter(PrintStream printStream) {
        this.printStream = printStream;
    }

    private String getID(String name, Map<String, Integer> map) {
        int number = map.getOrDefault(name, 0) + 1;
        map.put(name, number);
        return name + "_" + number;
    }

    private String getBasicBlockID(BasicBlock basicBlock) {
        String id = basicBlockMap.get(basicBlock);
        if (id == null) {
            if (basicBlock.getName() == null) {
                id = getID("basicBlock", basicBlockCounter);
            } else {
                id = getID(basicBlock.getName(), basicBlockCounter);
            }
            basicBlockMap.put(basicBlock, id);
        }
        return id;
    }

    private String getRegisterID(VirtualRegister virtualRegister) {
        String id = registerMap.get(virtualRegister);
        if (id == null) {
            if (virtualRegister.getName() == null) {
                id = getID("register", registerCounter);
            } else {
                id = getID(virtualRegister.getName(), registerCounter);
            }
            registerMap.put(virtualRegister, id);
        }
        return id;
    }

    private String getConstantDataID(ConstantData data) {
        String id = constantDataMap.get(data);
        if (id == null) {
            if (data.getName() == null) {
                id = getID("constantData", constantDataCounter);
            } else {
                id = getID(data.getName(), constantDataCounter);
            }
            constantDataMap.put(data, id);
        }
        return id;
    }


    @Override
    public void visit(IR ir) {
        isConstant = true;
        for (ConstantData constantData : ir.getConstantDataList()) {
            constantData.accept(this);
        }
        isConstant = false;
        for (ConstantString constantString : ir.getConstantStringMap().values()) {
            constantString.accept(this);
        }
        printStream.println();
        for (IRFunction irFunction : ir.getFunctionMap().values()){
            irFunction.accept(this);
        }
    }

    @Override
    public void visit(IRFunction function) {
        registerMap.clear();
        registerCounter.clear();
        printStream.print("func " + function.getName() + " ");
        for (VirtualRegister register : function.getParameters()) {
            printStream.print("$" + getRegisterID(register) + " ");
        }
        printStream.print("{\n");
        for (BasicBlock basicBlock : function.getReversePostOrder()) {
            basicBlock.accept(this);
        }
        printStream.print("}\n\n");

    }

    @Override
    public void visit(BasicBlock basicBlock) {
        if (visitedBasicBlock.contains(basicBlock)) return;
        visitedBasicBlock.add(basicBlock);
        printStream.println("%" + getBasicBlockID(basicBlock) + ":");
        for (IRInstruction instruction = basicBlock.getFirstInstruction(); instruction != null; instruction = instruction.getNextInstruction()) {
            printStream.print("[" + instruction.getLiveIn().size() + ", " + instruction.getLiveOut().size() + "]");
            instruction.accept(this);
        }
    }

    @Override
    public void visit(BranchInstruction instruction) {
        printStream.print("    br ");
        instruction.getCondition().accept(this);
        printStream.println(" %" + getBasicBlockID(instruction.getThenBasicBlock()) + " %" + getBasicBlockID(instruction.getElseBasicBlock()));
        printStream.println();

    }

    @Override
    public void visit(BinaryOperationInstruction instruction) {
        printStream.print("    ");
        String op = null;
        switch (instruction.getOperator()) {
            case ADD:
                op = "add";
                break;
            case SUB:
                op = "sub";
                break;
            case MUL:
                op = "mul";
                break;
            case DIV:
                op = "div";
                break;
            case MOD:
                op = "rem";
                break;
            case SHL:
                op = "shl";
                break;
            case SHR:
                op = "shr";
                break;
            case BITWISE_AND:
                op = "and";
                break;
            case BITWISE_OR:
                op = "or";
                break;
            case BITWISE_XOR:
                op = "xor";
                break;
        }
        instruction.getDestination().accept(this);
        printStream.print(" = " + op + " ");
        instruction.getLeftData().accept(this);
        printStream.print(" ");
        instruction.getRightData().accept(this);
        printStream.println();

    }

    @Override
    public void visit(ComparisonInstruction instruction) {
        printStream.print("    ");
        String op = null;
        switch (instruction.getOperator()) {
            case EQUAL:
                op = "seq";
                break;
            case INEQUAL:
                op = "sne";
                break;
            case GREATER:
                op = "sgt";
                break;
            case GREATER_EQUAL:
                op = "sge";
                break;
            case LESS:
                op = "slt";
                break;
            case LESS_EQUAL:
                op = "sle";
                break;
        }
        instruction.getDestination().accept(this);
        printStream.print(" = " + op + " ");
        instruction.getLeftData().accept(this);
        printStream.print(" ");
        instruction.getRightData().accept(this);
        printStream.println();

    }

    @Override
    public void visit(FunctionCallInstruction instruction) {
        printStream.print("    ");
        if (instruction.getDestination() != null) {
            instruction.getDestination().accept(this);
            printStream.print(" = ");
        }
        printStream.print("call " + instruction.getFunction().getName() + " ");
        for (Data parameter : instruction.getParameters()) {
            parameter.accept(this);
            printStream.print(" ");
        }
        printStream.println();

    }

    @Override
    public void visit(GotoInstruction instruction) {
        printStream.print("    jump %" + getBasicBlockID(instruction.getBasicBlock()) + "\n\n");
    }

    @Override
    public void visit(HeapAllocateInstruction instruction) {
        printStream.print("    ");
        instruction.getDestination().accept(this);
        printStream.print(" = alloc ");
        instruction.getSize().accept(this);
        printStream.println();
    }

    @Override
    public void visit(LoadInstruction instruction) {
        printStream.print("    ");
        instruction.getDestination().accept(this);
        printStream.print(" = load " + instruction.getSize() + " ");
        instruction.getAddress().accept(this);
        printStream.println(" " + instruction.getOffset());
    }

    @Override
    public void visit(MoveInstruction instruction) {
        printStream.print("    ");
        instruction.getDestination().accept(this);
        printStream.print(" = move ");
        instruction.getData().accept(this);
        printStream.println();

    }

    @Override
    public void visit(PopInstruction instruction) {
        printStream.print("   pop ");
        instruction.getRegister().accept(this);
        printStream.println();
    }

    @Override
    public void visit(PushInstruction instruction) {
        printStream.print("   push ");
        instruction.getData().accept(this);
        printStream.println();

    }

    @Override
    public void visit(ReturnInstruction instruction) {
        printStream.print("    ret ");
        if (instruction.getData() != null) {
            instruction.getData().accept(this);
        } else {
            printStream.print("0");
        }
        printStream.println();
        printStream.println();
    }

    @Override
    public void visit(StoreInstruction instruction) {
        printStream.print("    store " + instruction.getSize() + " ");
        instruction.getAddress().accept(this);
        printStream.print(" ");
        instruction.getData().accept(this);
        printStream.println(" " + instruction.getOffset());
    }

    @Override
    public void visit(UnaryOperationInstruction instruction) {
        printStream.print("    ");
        String op = null;
        switch (instruction.getOperator()) {
            case NEG:
                op = "neg";
                break;
            case BITWISE_NOT:
                op = "not";
                break;
        }
        instruction.getDestination().accept(this);
        printStream.printf(" = " + op + " ");
        instruction.getDestination().accept(this);
        printStream.println();

    }

    @Override
    public void visit(ConstantInt constant) {
        printStream.print(constant.getValue());
    }

    @Override
    public void visit(ConstantString constant) {
        if (isConstant) {
            printStream.print("asciiz @" + getConstantDataID(constant) + " " +  constant.getString() + "\n");
        }
        else {
            printStream.print("@" + getConstantDataID(constant));
        }
    }

    @Override
    public void visit(ConstantVariable constant) {
        if (isConstant) {
            printStream.print("space @" + getConstantDataID(constant) + " " + constant.getSize() + "\n");
        }
        else {
            printStream.print("@" + getConstantDataID(constant));
        }
    }

    @Override
    public void visit(VirtualRegister register) {
        printStream.print("$" + getRegisterID(register));
    }

    @Override
    public void visit(PhysicalRegister register) {
        printStream.print(register.getName());
    }
}
