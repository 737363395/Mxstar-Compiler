package cn.cyx666.Mxstar.IR;

import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.Function.*;
import cn.cyx666.Mxstar.IR.Instruction.*;

public interface IRVisitor {
    void visit(IR ir);
    void visit(IRFunction function);
    void visit(BasicBlock basicBlock);
    void visit(BinaryOperationInstruction instruction);
    void visit(BranchInstruction instruction);
    void visit(ComparisonInstruction instruction);
    void visit(FunctionCallInstruction instruction);
    void visit(GotoInstruction instruction);
    void visit(HeapAllocateInstruction instruction);
    void visit(LoadInstruction instruction);
    void visit(MoveInstruction instruction);
    void visit(PopInstruction instruction);
    void visit(PushInstruction instruction);
    void visit(ReturnInstruction instruction);
    void visit(StoreInstruction instruction);
    void visit(UnaryOperationInstruction instruction);
    void visit(VirtualRegister register);
    void visit(PhysicalRegister register);
    void visit(ConstantInt constant);
    void visit(ConstantVariable constant);
    void visit(ConstantString constant);
}
