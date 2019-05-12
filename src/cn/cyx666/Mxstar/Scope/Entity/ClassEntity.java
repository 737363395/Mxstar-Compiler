package cn.cyx666.Mxstar.Scope.Entity;

import cn.cyx666.Mxstar.Scope.Scope;
import cn.cyx666.Mxstar.Type.*;
import cn.cyx666.Mxstar.AST.DeclarationNode.*;

public class ClassEntity extends Entity{
    private Scope scope;
    private int size;

    public ClassEntity(String name, Type type, Scope parent) {
        super(name, type);
        scope = new Scope(parent, true);
    }

    public ClassEntity(ClassDeclarationNode node, Scope parent) {
        super(node.getName(), new ClassType(node.getName()));
        scope = new Scope(parent, true);
        for (FunctionDeclarationNode function : node.getFunctions()) {
            scope.putCheck(function.position(), function.getName(), Scope.functionKey(function.getName()), new FunctionEntity(function, node.getName()));
        }
    }

    public Scope getScope() {
        return scope;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
