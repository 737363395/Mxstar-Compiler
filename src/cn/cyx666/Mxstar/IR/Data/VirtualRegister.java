package cn.cyx666.Mxstar.IR.Data;

import cn.cyx666.Mxstar.IR.*;

import java.util.HashSet;
import java.util.Set;

public class VirtualRegister extends Register {
    private String name = null;
    private PhysicalRegister physicalRegister = null;
    private Set<VirtualRegister> neighbor = new HashSet<>();
    private Register color = null;
    private int degree = 0;
    private Set<VirtualRegister> sameRegister = new HashSet<>();
    private boolean isRemoved = false;

    public VirtualRegister(){}

    public VirtualRegister(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public PhysicalRegister getPhysicalRegister() {
        return physicalRegister;
    }

    public void setPhysicalRegister(PhysicalRegister physicalRegister) {
        this.physicalRegister = physicalRegister;
    }

    public Register getColor() {
        return color;
    }

    public void setColor(Register color) {
        this.color = color;
    }

    public void addDegree(int n) {
        degree += n;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public Set<VirtualRegister> getSameRegister() {
        return sameRegister;
    }

    public Set<VirtualRegister> getNeighbor() {
        return neighbor;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    public void resetColor() {
        neighbor.clear();
        color = null;
        degree = 0;
        sameRegister.clear();
    }

    @Override
    public VirtualRegister copy() {
        return new VirtualRegister(name);
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
