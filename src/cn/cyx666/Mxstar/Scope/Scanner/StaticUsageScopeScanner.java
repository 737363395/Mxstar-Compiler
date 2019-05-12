package cn.cyx666.Mxstar.Scope.Scanner;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.DeclarationNode.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;
import cn.cyx666.Mxstar.AST.ProgramNode.*;
import cn.cyx666.Mxstar.AST.StatementNode.*;
import cn.cyx666.Mxstar.Scope.Entity.*;
import cn.cyx666.Mxstar.Scope.*;
import cn.cyx666.Mxstar.Type.*;

import java.util.HashSet;
import java.util.Set;

public class StaticUsageScopeScanner extends ScopeScanner{
    private Set<VariableEntity> usedStaticSet = new HashSet<>(), unUsedStaticSet = new HashSet<>();
    private Scope globalScope, currentScope;
    private boolean inDefinition = false;

    public StaticUsageScopeScanner(Scope globalScope) {
        this.globalScope = globalScope;
    }

    @Override
    public void visit(ProgramNode node) {
        currentScope = globalScope;
        for (DeclarationNode declaration : node.getDeclarations()) {
            if (declaration instanceof FunctionDeclarationNode || declaration instanceof VariableDeclarationNode) declaration.accept(this);
            else {
                ClassEntity entity = (ClassEntity) currentScope.get(Scope.classKey(declaration.getName()));
                currentScope = entity.getScope();
                for (FunctionDeclarationNode memberFunc : ((ClassDeclarationNode) declaration).getFunctions()) {
                    memberFunc.accept(this);
                }
                currentScope = currentScope.getParent();
            }
        }
        for (VariableEntity entity : unUsedStaticSet) {
            if (usedStaticSet.contains(entity)) continue;
            entity.setUsed(false);
        }
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        if (node.getInitialization() != null) {
            VariableEntity entity = (VariableEntity) currentScope.get(Scope.variableKey(node.getName()));
            if (entity.getType() instanceof ArrayType || entity.isGlobal()) {
                unUsedStaticSet.add(entity);
            }
            node.getInitialization().accept(this);
        }
    }

    @Override
    public void visit(BlockStatementNode node) {
        currentScope = node.getScope();
        for (ASTNode statement : node.getStatements()) statement.accept(this);
        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(ConditionStatementNode node) {
        node.getCondition().accept(this);
        if (node.getThenStatement() != null) node.getThenStatement().accept(this);
        if (node.getElseStatement() != null) node.getElseStatement().accept(this);
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        node.getBlock().accept(this);
    }

    @Override
    public void visit(ExpressionStatementNode node) {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(ReturnStatementNode node) {
        if (node.getExpression() != null) node.getExpression().accept(this);
    }

    @Override
    public void visit(SuffixExpressionNode node) {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(PrefixExpressionNode node) {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(MemberAccessExpressionNode node) {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(SubscriptExpressionNode node) {
        if (inDefinition) {
            node.getArray().accept(this);
            inDefinition = false;
            node.getSubscript().accept(this);
            inDefinition = true;
        } else {
            node.getArray().accept(this);
            node.getSubscript().accept(this);
        }
    }

    @Override
    public void visit(BinaryExpressionNode node) {
        node.getLeftChild().accept(this);
        node.getRightChild().accept(this);
    }

    @Override
    public void visit(NewExpressionNode node) {
        if (node.getDimensions() != null) for (ExpressionNode dimension : node.getDimensions()) dimension.accept(this);
    }

    @Override
    public void visit(ForStatementNode node) {
        if (node.getInitialization() != null) node.getInitialization().accept(this);
        if (node.getCondition() != null) node.getCondition().accept(this);
        if (node.getStep() != null) node.getStep().accept(this);
        if (node.getStatements() != null) node.getStatements().accept(this);
    }

    @Override
    public void visit(WhileStatementNode node) {
        node.getCondition().accept(this);
        if (node.getStatement() != null) node.getStatement().accept(this);
    }

    @Override
    public void visit(FunctionCallExpressionNode node) {
        node.getFunction().accept(this);
        for (ExpressionNode parameter : node.getParameters()) parameter.accept(this);
    }

    @Override
    public void visit(AssignExpressionNode node) {
        if (node.getRightChild().getType() instanceof ArrayType && !(node.getRightChild() instanceof NewExpressionNode)) {
            node.getLeftChild().accept(this);
            node.getRightChild().accept(this);
            return;
        }
        inDefinition = true;
        node.getLeftChild().accept(this);
        inDefinition = false;
        node.getRightChild().accept(this);
    }

    @Override
    public void visit(IdentifierExpressionNode node) {
        VariableEntity entity = (VariableEntity) currentScope.get(Scope.variableKey(node.getIdentifier()));
        if (entity != null) {
            if (entity.getType() instanceof ArrayType || entity.isGlobal()) {
                if (inDefinition) unUsedStaticSet.add(entity);
                else usedStaticSet.add(entity);
            }
        }
    }
}
