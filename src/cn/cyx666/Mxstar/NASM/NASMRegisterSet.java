package cn.cyx666.Mxstar.NASM;
import cn.cyx666.Mxstar.IR.Data.*;

import java.util.*;

public class NASMRegisterSet {
    public static final Collection<PhysicalRegister> allRegister, generalRegister, callerSaveRegister, calleeSaveRegister;
    public static final PhysicalRegister rax, rcx, rdx, rbx, rsi, rdi, rsp, rbp, r8, r9, r10, r11, r12, r13, r14, r15;
    public static final List<PhysicalRegister> arg6;

    static {
        List<PhysicalRegister> all = new ArrayList<>();
        List<PhysicalRegister> general = new ArrayList<>();
        List<PhysicalRegister> callerSave = new ArrayList<>();
        List<PhysicalRegister> calleeSave = new ArrayList<>();

        rax = new PhysicalRegister("rax", false, true, false, -1);
        rcx = new PhysicalRegister("rcx", false, true, false, 3);
        rdx = new PhysicalRegister("rdx", false, true, false, 2);
        rbx = new PhysicalRegister("rbx", false, false, true, -1);
        rsi = new PhysicalRegister("rsi", false, true, false, 1);
        rdi = new PhysicalRegister("rdi", false, true, false, 0);
        rsp = new PhysicalRegister("rsp", false, true, false, -1);
        rbp = new PhysicalRegister("rbp", false, false, true, -1);
        // r8 and r9 are actually general registers
        r8 = new PhysicalRegister("r8", true, true, false, 4);
        r9 = new PhysicalRegister("r9", true, true, false, 5);
        r10 = new PhysicalRegister("r10", true, true, false, -1);
        r11 = new PhysicalRegister("r11", true, true, false, -1);
        r12 = new PhysicalRegister("r12", true, false, true, -1);
        r13 = new PhysicalRegister("r13", true, false, true, -1);
        r14 = new PhysicalRegister("r14", true, false, true, -1);
        r15 = new PhysicalRegister("r15", true, false, true, -1);

        arg6 = new ArrayList<>();
        arg6.add(rdi);
        arg6.add(rsi);
        arg6.add(rdx);
        arg6.add(rcx);
        arg6.add(r8);
        arg6.add(r9);

        all.add(rax);
        all.add(rcx);
        all.add(rdx);
        all.add(rbx);
        all.add(rsi);
        all.add(rdi);
        all.add(rsp);
        all.add(rbp);
        all.add(r8);
        all.add(r9);
        all.add(r10);
        all.add(r11);
        all.add(r12);
        all.add(r13);
        all.add(r14);
        all.add(r15);

        all.stream().filter(PhysicalRegister::isGeneral).forEach(general::add);
        all.stream().filter(PhysicalRegister::isCallerSave).forEach(callerSave::add);
        all.stream().filter(PhysicalRegister::isCalleeSave).forEach(calleeSave::add);

        allRegister = Collections.unmodifiableCollection(all);
        generalRegister = Collections.unmodifiableCollection(general);
        callerSaveRegister = Collections.unmodifiableList(callerSave);
        calleeSaveRegister = Collections.unmodifiableList(calleeSave);
    }
}