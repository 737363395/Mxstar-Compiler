package cn.cyx666.Mxstar.NASM;

import cn.cyx666.Mxstar.IR.BasicBlock.BasicBlock;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.Function.IRFunction;
import cn.cyx666.Mxstar.IR.IR;
import cn.cyx666.Mxstar.IR.IRVisitor;
import cn.cyx666.Mxstar.IR.Instruction.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class NASMPrinter implements IRVisitor {
    private PrintStream printStream;
    private Map<String, Integer> counter = new HashMap<>();
    private Map<Object, String> map = new HashMap<>();
    private PhysicalRegister register0, register1;

    public NASMPrinter(PrintStream printStream) {
        this.printStream = printStream;
    }

    private boolean isBssSection, isDataSection;

    private String newId(String id) {
        int nowCnt = counter.getOrDefault(id, 0) + 1;
        counter.put(id, nowCnt);
        return id + "_" + nowCnt;
    }

    private String dataId(ConstantData data) {
        String id = map.get(data);
        if (id == null) {
            id = "__static_data_" + newId(data.getName());
            map.put(data, id);
        }
        return id;
    }

    private String basicBlockId(BasicBlock basicBlock) {
        String id = map.get(basicBlock);
        if (id == null) {
            id = "__block_" + newId(basicBlock.getName());
            map.put(basicBlock, id);
        }
        return id;
    }

    @Override
    public void visit(IR ir) {
        register0 = ir.getRegister0();
        register1 = ir.getRegister1();
        map.put(ir.getFunctionMap().get("main").getStartBasicBlock(), "main");

        printStream.println("\t\tglobal\tmain");
        printStream.println();

        printStream.println("\t\textern\tmalloc");
        printStream.println();

        if (ir.getConstantDataList().size() > 0) {
            isBssSection = true;
            printStream.println("\t\tsection\t.bss");
            for (ConstantData constantData : ir.getConstantDataList()) {
                constantData.accept(this);
            }
            printStream.println();
            isBssSection = false;
        }

        if (ir.getConstantStringMap().size() > 0) {
            isDataSection = true;
            printStream.println("\t\tsection\t.data");
            for (ConstantString constantString : ir.getConstantStringMap().values()) {
                constantString.accept(this);
            }
            printStream.println();
            isDataSection = false;
        }

        printStream.println("\t\tsection\t.text\n");
        for (IRFunction function : ir.getFunctionMap().values()) {
            function.accept(this);
        }

        printStream.println();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("lib/builtin_functions.asm"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                printStream.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void visit(IRFunction function) {
        printStream.println("# function " + function.getName() + "\n");
        for (BasicBlock basicBlock: function.getReversePostOrder()) {
            basicBlock.accept(this);
        }
    }

    @Override
    public void visit(BasicBlock basicBlock) {
        printStream.println(basicBlockId(basicBlock) + ":");
        for (IRInstruction instruction = basicBlock.getFirstInstruction(); instruction != null; instruction = instruction.getNextInstruction()) {
            instruction.accept(this);
        }
        printStream.println();
    }

    @Override
    public void visit(BranchInstruction instruction) {
        if (instruction.getCondition() instanceof ConstantInt) {
            int bool = ((ConstantInt) instruction.getCondition()).getValue();
            printStream.println("\t\tjmp\t\t" + (bool == 1? basicBlockId(instruction.getThenBasicBlock()) : basicBlockId(instruction.getElseBasicBlock())));
            return;
        }
        printStream.print("\t\tcmp\t\t");
        instruction.getCondition().accept(this);
        printStream.println(", 1");
        printStream.println("\t\tje\t\t" + basicBlockId(instruction.getThenBasicBlock()));
        if (instruction.getElseBasicBlock().getIndex() + 1 == instruction.getParentBasicBlock().getIndex()) return;
        printStream.println("\t\tjmp\t\t" + basicBlockId(instruction.getElseBasicBlock()));
    }

    @Override
    public void visit(GotoInstruction instruction) {
        if (instruction.getBasicBlock().getIndex() + 1 == instruction.getParentBasicBlock().getIndex()) return;
        printStream.println("\t\tjmp\t\t" + basicBlockId(instruction.getBasicBlock()));
    }

    @Override
    public void visit(ReturnInstruction instruction) {
        printStream.println("\t\tret");
    }

    @Override
    public void visit(UnaryOperationInstruction instruction) {
        String operator = null;
        switch (instruction.getOperator()) {
            case BITWISE_NOT:
                operator = "not";
                break;
            case NEG:
                operator = "neg";
                break;
        }
        printStream.print("\t\tmov\t\t");
        instruction.getDestination().accept(this);
        printStream.print(", ");
        instruction.getData().accept(this);
        printStream.print("\n\t\t" + operator + "\t\t");
        instruction.getDestination().accept(this);
        printStream.println();
    }

    private String sizeStr(int memSize) {
        String sizeStr = null;
        switch (memSize) {
            case 1:
                sizeStr = "byte";
                break;
            case 2:
                sizeStr = "word";
                break;
            case 4:
                sizeStr = "dword";
                break;
            case 8:
                sizeStr = "qword";
                break;
        }
        return sizeStr;
    }

    @Override
    public void visit(LoadInstruction instruction) {
        if (instruction.getAddress() instanceof ConstantString) {
            printStream.print("\t\tmov\t\t");
            instruction.getDestination().accept(this);
            printStream.print(", " + sizeStr(instruction.getSize()) + " ");
            instruction.getAddress().accept(this);
            printStream.println();
            return;
        }
        printStream.print("\t\tmov\t\t");
        instruction.getDestination().accept(this);
        printStream.print(", " + sizeStr(instruction.getSize()) + " [");
        instruction.getAddress().accept(this);
        if (instruction.getOffset() < 0) {
            printStream.print(instruction.getOffset());
        } else {
            printStream.print("+" + instruction.getOffset());
        }
        printStream.println("]");
    }

    @Override
    public void visit(BinaryOperationInstruction instruction) {
        if (instruction.getOperator() == BinaryOperationInstruction.BinaryOperator.DIV ||
                instruction.getOperator() == BinaryOperationInstruction.BinaryOperator.MOD) {
            printStream.print("\t\tmov\t\trbx, ");
            instruction.getRightData().accept(this);
            printStream.println();
            printStream.print("\t\tmov\t\trax, ");
            instruction.getLeftData().accept(this);
            printStream.println();
            printStream.println("\t\tmov\t\t" + register0.getName() + ", rdx");
            printStream.println("\t\tcdq");
            printStream.println("\t\tidiv\tebx");
            printStream.println("\t\tmovsx\trax, eax");
            printStream.println("\t\tmovsx\trdx, edx");
            printStream.print("\t\tmov\t\t");
            instruction.getDestination().accept(this);
            if (instruction.getOperator() == BinaryOperationInstruction.BinaryOperator.DIV) {
                printStream.println(", rax");
            } else {
                printStream.println(", rdx");
            }
            printStream.println("\t\tmov\t\trdx, " + register0.getName());
        } else if (instruction.getOperator() == BinaryOperationInstruction.BinaryOperator.SHL ||
                instruction.getOperator() == BinaryOperationInstruction.BinaryOperator.SHR) {
            printStream.println("\t\tmov\t\trbx, rcx");
            printStream.print("\t\tmov\t\trcx, ");
            instruction.getRightData().accept(this);
            if (instruction.getOperator() == BinaryOperationInstruction.BinaryOperator.SHL) {
                printStream.print("\n\t\tsal\t\t");
            } else {
                printStream.print("\n\t\tsar\t\t");
            }
            instruction.getLeftData().accept(this);
            printStream.println(", cl");
            printStream.println("\t\tmov\t\trcx, rbx");
            printStream.print("\t\tand\t\t");
            instruction.getLeftData().accept(this);
            printStream.println(", -1");
        } else {
            String op = null;
            switch (instruction.getOperator()) {
                case ADD:
                    if (instruction.getRightData() instanceof ConstantInt &&
                            ((ConstantInt) instruction.getRightData()).getValue() == 1) {
                        printStream.print("\t\tinc\t\t");
                        instruction.getLeftData().accept(this);
                        printStream.println();
                        return;
                    }
                    op = "add\t";
                    break;
                case SUB:
                    if (instruction.getRightData() instanceof ConstantInt &&
                            ((ConstantInt) instruction.getRightData()).getValue() == 1) {
                        printStream.print("\t\tdec\t\t");
                        instruction.getLeftData().accept(this);
                        printStream.println();
                        return;
                    }
                    op = "sub\t";
                    break;
                case MUL:
                    if (instruction.getRightData() instanceof ConstantInt &&
                            ((ConstantInt) instruction.getRightData()).getValue() == 1) {
                        return;
                    }
                    op = "imul";
                    break;
                case BITWISE_OR:
                    op = "or\t";
                    break;
                case BITWISE_XOR:
                    op = "xor\t";
                    break;
                case BITWISE_AND:
                    op = "and\t";
                    break;
            }
            printStream.print("\t\t" + op + "\t");
            instruction.getLeftData().accept(this);
            printStream.print(", ");
            instruction.getRightData().accept(this);
            printStream.println();
        }
    }

    @Override
    public void visit(ComparisonInstruction instruction) {
        if (instruction.getLeftData() instanceof PhysicalRegister) {
            printStream.print("\t\tand\t\t");
            instruction.getLeftData().accept(this);
            printStream.println(", -1");
        }
        if (instruction.getRightData() instanceof PhysicalRegister) {
            printStream.print("\t\tand\t\t");
            instruction.getRightData().accept(this);
            printStream.println(", -1");
        }
        printStream.println("\t\txor\t\trax, rax");
        printStream.print("\t\tcmp\t\t");
        instruction.getLeftData().accept(this);
        printStream.print(", ");
        instruction.getRightData().accept(this);
        printStream.println();
        String operator = null;
        switch (instruction.getOperator()) {
            case EQUAL:
                operator = "sete";
                break;
            case INEQUAL:
                operator = "setne";
                break;
            case LESS:
                operator = "setl";
                break;
            case LESS_EQUAL:
                operator = "setle";
                break;
            case GREATER:
                operator = "setg";
                break;
            case GREATER_EQUAL:
                operator = "setge";
                break;
        }
        printStream.println("\t\t" + operator + "\tal");
        printStream.print("\t\tmov\t\t");
        instruction.getDestination().accept(this);
        printStream.println(", rax");
    }

    @Override
    public void visit(MoveInstruction instruction) {
        printStream.print("\t\tmov\t\t");
        instruction.getDestination().accept(this);
        printStream.print(", ");
        instruction.getData().accept(this);
        printStream.println();
    }

    @Override
    public void visit(StoreInstruction instruction) {
        if (instruction.getAddress() instanceof ConstantString) {
            printStream.print("\t\tmov\t\t");
            instruction.getAddress().accept(this);
            printStream.print(", " + sizeStr(instruction.getSize()) + " ");
            instruction.getData().accept(this);
            printStream.println();
            return;
        }
        printStream.print("\t\tmov\t\t" + sizeStr(instruction.getSize()) + " [");
        instruction.getAddress().accept(this);
        if (instruction.getOffset() < 0) {
            printStream.print(instruction.getOffset());
        } else {
            printStream.print("+" + instruction.getOffset());
        }
        printStream.print("], ");
        instruction.getData().accept(this);
        printStream.println();
    }

    @Override
    public void visit(FunctionCallInstruction instruction) {
        if (instruction.getFunction().isBuiltIn()) {
            printStream.println("\t\tcall\t" + instruction.getFunction().getBuiltInCall());
        } else {
            printStream.println("\t\tcall\t" + basicBlockId(instruction.getFunction().getStartBasicBlock()));
        }
    }

    @Override
    public void visit(HeapAllocateInstruction instruction) {
        printStream.println("\t\tcall\tmalloc");
    }

    @Override
    public void visit(PushInstruction instruction) {
        printStream.print("\t\tpush\t");
        instruction.getData().accept(this);
        printStream.println();
    }

    @Override
    public void visit(PopInstruction instruction) {
        printStream.print("\t\tpop\t");
        instruction.getRegister().accept(this);
        printStream.println();
    }

    @Override
    public void visit(ConstantInt constant) {
        printStream.print(constant.getValue());
    }

    @Override
    public void visit(PhysicalRegister register) {
        printStream.print(register.getName());
    }

    @Override
    public void visit(ConstantVariable constant) {
        if (isBssSection) {
            String operator = null;
            switch (constant.getSize()) {
                case 1:
                    operator = "resb";
                    break;
                case 2:
                    operator = "resw";
                    break;
                case 4:
                    operator = "resd";
                    break;
                case 8:
                    operator = "resq";
                    break;
            }
            printStream.println(dataId(constant) + ":\t" + operator + "\t1");
        } else {
            printStream.print(dataId(constant));
        }
    }

    @Override
    public void visit(ConstantString constant) {
        if (isDataSection) {
            printStream.println(dataId(constant) + ":");
            printStream.println("\t\tdq\t\t" + constant.getString().length());
            printStream.println("\t\tdb\t\t" + staticStrDataSection(constant.getString()));
        } else {
            printStream.print(dataId(constant));
        }
    }

    private String staticStrDataSection(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, n = str.length(); i < n; ++i) {
            char c = str.charAt(i);
            sb.append((int) c);
            sb.append(", ");
        }
        sb.append(0);
        return sb.toString();
    }

    @Override
    public void visit(VirtualRegister register) {
        System.err.println(342342);
    }
}
