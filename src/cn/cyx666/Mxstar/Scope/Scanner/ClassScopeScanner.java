package cn.cyx666.Mxstar.Scope.Scanner;

import cn.cyx666.Mxstar.AST.DeclarationNode.*;
import cn.cyx666.Mxstar.AST.ProgramNode.*;
import cn.cyx666.Mxstar.Scope.Entity.*;
import cn.cyx666.Mxstar.Scope.*;
import cn.cyx666.Mxstar.Type.*;

public class ClassScopeScanner extends ScopeScanner {
    private Scope globalScope, currentScope;
    private int currentOffset = 0;

    public ClassScopeScanner(Scope globalScope) {
        this.globalScope = globalScope;
    }

    @Override
    public void visit(ProgramNode node) {
        for (DeclarationNode declaration : node.getDeclarations()) {
            if (declaration instanceof ClassDeclarationNode) declaration.accept(this);
        }
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        ClassEntity entity = (ClassEntity) globalScope.getCheck(node.position(), node.getName(), Scope.classKey(node.getName()));
        currentScope = entity.getScope();
        for (VariableDeclarationNode declaration : node.getVariables()) declaration.accept(this);
        entity.setSize(currentOffset);
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        VariableEntity entity;
        if (node.getType().getType() instanceof ClassType) {
            String className = ((ClassType) node.getType().getType()).getName();
            currentScope.assertContainsExactKey(node.position(), className, Scope.classKey(className));
        }
        checkVariableInitialization(node);
        entity = new VariableEntity(node.getName(), node.getType().getType());
        entity.setAddressOffset(currentOffset);
        currentOffset += node.getType().getType().getSize();
        currentScope.putCheck(node.position(), node.getName(), Scope.variableKey(node.getName()), entity);
    }
}
