package cn.cyx666.Mxstar.Scope.Scanner;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.DeclarationNode.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;
import cn.cyx666.Mxstar.AST.ProgramNode.*;
import cn.cyx666.Mxstar.AST.StatementNode.*;
import cn.cyx666.Mxstar.Error.*;
import cn.cyx666.Mxstar.Scope.*;
import cn.cyx666.Mxstar.Scope.Entity.*;
import cn.cyx666.Mxstar.Type.*;

public class FunctionScopeScanner extends ScopeScanner {
    private Scope globalScope, currentScope;
    private int loopDepth;
    private Type currentReturnType;
    private ClassType currentClassType;
    private FunctionEntity currentFunctionEntity;

    public FunctionScopeScanner(Scope globalScope) {
        this.globalScope = globalScope;
    }

    @Override
    public void visit(ProgramNode node) {
        currentScope = globalScope;
        loopDepth = 0;
        for (DeclarationNode declaration : node.getDeclarations()) {
            if (declaration instanceof ClassDeclarationNode || declaration instanceof VariableDeclarationNode || declaration instanceof FunctionDeclarationNode) declaration.accept(this);
        }
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        VariableEntity entity = new VariableEntity(node.getName(), node.getType().getType());
        if (node.getType().getType() instanceof ClassType) {
            String className = ((ClassType) node.getType().getType()).getName();
            currentScope.assertContainsExactKey(node.position(), className, Scope.classKey(className));
        }
        checkVariableInitialization(node);
        if (globalScope == currentScope) entity.setGlobal(true);
        currentScope.putCheck(node.position(), node.getName(), Scope.variableKey(node.getName()), entity);
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        FunctionEntity entity = (FunctionEntity) currentScope.getCheck(node.position(), node.getName(), Scope.functionKey(node.getName()));
        if (entity.getReturnType() instanceof ClassType)
            currentScope.assertContainsExactKey(node.getReturnType().position(), ((ClassType) entity.getReturnType()).getName(), Scope.classKey(((ClassType) entity.getReturnType()).getName()));
        currentReturnType = entity.getReturnType();
        node.getBlock().initializeScope(currentScope);
        currentScope = node.getBlock().getScope();
        if (currentClassType != null) {
            String key = Scope.variableKey(Scope.THIS_PARAMETERS_NAME);
            currentScope.putCheck(node.position(), Scope.THIS_PARAMETERS_NAME, key, new VariableEntity(Scope.THIS_PARAMETERS_NAME, currentClassType));
            if (node.isConstructor() && !(node.getName().equals(currentClassType.getName()))) throw new SemanticError(node.position(), "Invalid return type for function");
        }
        for (VariableDeclarationNode variable : node.getParameters()) {
            variable.accept(this);
        }
        currentScope = currentScope.getParent();
        node.getBlock().accept(this);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        ClassEntity entity = (ClassEntity) currentScope.getCheck(node.position(), node.getName(), Scope.classKey(node.getName()));
        currentScope = entity.getScope();
        currentClassType = (ClassType) entity.getType();
        for (FunctionDeclarationNode function : node.getFunctions()) function.accept(this);
        currentClassType = null;
        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(BlockStatementNode node) {
        currentScope = node.getScope();
        for (ASTNode statement : node.getStatements()) {
            if (statement instanceof StatementNode) {
                if (statement instanceof BlockStatementNode) ((BlockStatementNode) statement).initializeScope(currentScope);
                statement.accept(this);
            }
            else if (statement instanceof VariableDeclarationNode) statement.accept(this);
        }
        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(ExpressionStatementNode node) {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(ConditionStatementNode node) {
        node.getCondition().accept(this);
        if (!(node.getCondition().getType() instanceof BoolType))
            throw new SemanticError(node.getCondition().position(), "Invalid condition expression for \"if\" statement");
        if (node.getThenStatement() != null) {
            if (node.getThenStatement() instanceof BlockStatementNode)
                ((BlockStatementNode) node.getThenStatement()).initializeScope(currentScope);
            node.getThenStatement().accept(this);
        }
        if (node.getElseStatement() != null) {
            if (node.getElseStatement() instanceof BlockStatementNode)
                ((BlockStatementNode) node.getElseStatement()).initializeScope(currentScope);
            node.getElseStatement().accept(this);
        }
    }

    @Override
    public void visit(WhileStatementNode node) {
        ++loopDepth;
        node.getCondition().accept(this);
        if (!(node.getCondition().getType() instanceof BoolType))
            throw new SemanticError(node.getCondition().position(), "Invalid condition expression for \"while\" statement");
        if (node.getStatement() != null) {
            if (node.getStatement() instanceof BlockStatementNode) ((BlockStatementNode) node.getStatement()).initializeScope(currentScope);
            node.getStatement().accept(this);
        }
        --loopDepth;
    }

    @Override
    public void visit(ForStatementNode node) {
        ++loopDepth;
        if (node.getInitialization() != null) node.getInitialization().accept(this);
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
            if (!(node.getCondition().getType() instanceof BoolType))
                throw new SemanticError(node.getCondition().position(), "Invalid condition expression for \"for\" statement");
        }
        if (node.getStep() != null) node.getStep().accept(this);
        if (node.getStatements() != null) {
            if (node.getStatements() instanceof BlockStatementNode) ((BlockStatementNode) node.getStatements()).initializeScope(currentScope);
            node.getStatements().accept(this);
        }
        --loopDepth;
    }

    @Override
    public void visit(ContinueStatementNode node) {
        if (loopDepth <= 0) throw new SemanticError(node.position(), "Invalid \"continue\" statement out of loop");
    }

    @Override
    public void visit(BreakStatementNode node) {
        if (loopDepth <= 0) throw new SemanticError(node.position(), "Invalid \"break\" statement out of loop");
    }

    @Override
    public void visit(ReturnStatementNode node) {
        boolean bool;
        if (node.getExpression() == null) bool = !(currentReturnType == null || currentReturnType instanceof VoidType);
        else {
            node.getExpression().accept(this);
            if (node.getExpression().getType() == null || node.getExpression().getType() instanceof VoidType) bool = true;
            else if (node.getExpression().getType() instanceof  NullType) bool = !(currentReturnType instanceof ClassType || currentReturnType instanceof ArrayType);
            else bool = !(node.getExpression().getType().equals(currentReturnType));
        }
        if (bool) throw new SemanticError(node.position(), "Invalid return type for \"return\" statement");
    }

    @Override
    public void visit(FunctionCallExpressionNode node) {
        FunctionEntity entity;
        int n, m;
        boolean bool = false;
        node.getFunction().accept(this);
        if (!(node.getFunction().getType() instanceof FunctionType)) throw new SemanticError(node.getFunction().position(), "Invalid call of non-function type");
        entity = currentFunctionEntity;
        n = entity.getParameters().size();
        m = entity.isMember() ? 1 : 0;
        node.setEntity(entity);
        if (n - m != node.getParameters().size()) throw new SemanticError(node.position(), "Invalid number of parameters");
        for (int i = 0; i < n - m; ++i) {
            node.getParameters().get(i).accept(this);
            if (node.getParameters().get(i).getType() instanceof  VoidType) bool = true;
            else if (node.getParameters().get(i).getType() instanceof NullType) bool = !(entity.getParameters().get(i + m).getType() instanceof ClassType || entity.getParameters().get(i + m).getType() instanceof StringType);
            else if (!node.getParameters().get(i).getType().equals(entity.getParameters().get(i + m).getType())) bool = true;
            if (bool) throw new SemanticError(node.getParameters().get(i).position(), "Invalid type for parameter");
        }
        node.setType(entity.getReturnType());
        node.setLeftValue(false);
    }

    @Override
    public void visit(SubscriptExpressionNode node) {
        node.getArray().accept(this);
        if (!(node.getArray().getType() instanceof ArrayType)) throw new SemanticError(node.position(), "Invalid subscript for non-array type");
        node.getSubscript().accept(this);
        if (!(node.getSubscript().getType() instanceof  IntType)) throw new SemanticError(node.position(), "Invalid subscript for array type");
        node.setType(((ArrayType) node.getArray().getType()).getBaseType());
        node.setLeftValue(true);
    }

    @Override
    public void visit(MemberAccessExpressionNode node) {
        String name;
        Entity memberEntity;
        ClassEntity classEntity;
        node.getExpression().accept(this);
        if (node.getExpression().getType() instanceof ClassType) name = ((ClassType) node.getExpression().getType()).getName();
        else if (node.getExpression().getType() instanceof  StringType) name = Scope.STRING_CLASS_NAME;
        else if (node.getExpression().getType() instanceof ArrayType) name = Scope.ARRAY_CLASS_NAME;
        else throw new SemanticError(node.position(), "Invalid member access for non-class type");
        classEntity = (ClassEntity) currentScope.getCheck(name, Scope.classKey(name));
        if (classEntity.getScope().containsExactKeyLocal(Scope.variableKey(node.getMember()))) memberEntity = classEntity.getScope().getLocal(Scope.variableKey(node.getMember()));
        else {
            memberEntity = classEntity.getScope().getCheckLocal(node.getMember(), Scope.functionKey(node.getMember()));
            currentFunctionEntity = (FunctionEntity) memberEntity;
        }
        node.setType(memberEntity.getType());
        node.setLeftValue(true);
    }

    @Override
    public void visit(SuffixExpressionNode node) {
        node.getExpression().accept(this);
        if (!(node.getExpression().getType() instanceof IntType)) throw new SemanticError(node.position(), "Invalid type for suffix operator");
        if (!(node.getExpression().isLeftValue())) throw new SemanticError(node.position(), "Invalid suffix operator for non-left-value type");
        node.setType(IntType.getInstance());
        node.setLeftValue(false);
    }

    @Override
    public void visit(PrefixExpressionNode node) {
        ExpressionNode expression = node.getExpression();
        node.getExpression().accept(this);
        switch (node.getOperator()){
            case PREFIX_INC:
            case PREFIX_DEC:
                if (!(expression.getType() instanceof IntType)) throw new SemanticError(node.position(), "Invalid type for " + node.getOperator().name() + " operator");
                if (!(expression.isLeftValue())) throw new SemanticError(node.position(), "Invalid non-left-value type for " + node.getOperator().name() + " operator");
                node.setType(IntType.getInstance());
                node.setLeftValue(true);
                break;
            case POS:
            case NEG:
            case BITWISE_NOT:
                if (!(expression.getType() instanceof IntType)) throw new SemanticError(node.position(), "Invalid type for " + node.getOperator().name() + " operator");
                node.setType(IntType.getInstance());
                node.setLeftValue(true);
                break;
            case LOGIC_NOT:
                if (!(node.getExpression().getType() instanceof BoolType)) throw new SemanticError(node.position(), "Invalid type for " + node.getOperator().name() + " operator");
                node.setType(BoolType.getInstance());
                node.setLeftValue(false);
                break;
        }
    }

    @Override
    public void visit(NewExpressionNode node) {
        if (node.getDimensions() != null) {
            for (ExpressionNode dimension : node.getDimensions()) {
                dimension.accept(this);
                if (!(dimension.getType() instanceof IntType)) throw new SemanticError(node.position(), "Invalid type for array dimension size");
            }
        }
        node.setType(node.getNewType().getType());
        node.setLeftValue(false);
    }

    @Override
    public void visit(BinaryExpressionNode node) {
        ExpressionNode leftChild = node.getLeftChild();
        ExpressionNode rightChild = node.getRightChild();
        leftChild.accept(this);
        rightChild.accept(this);
        switch (node.getOperator()) {
            case MUL:
            case DIV:
            case MOD:
            case ADD:
                if (leftChild.getType() instanceof  StringType && rightChild.getType() instanceof StringType){
                    node.setType(StringType.getInstance());
                    node.setLeftValue(false);
                    break;
                }
            case SUB:
            case SHL:
            case SHR:
            case BITWISE_OR:
            case BITWISE_AND:
            case BITWISE_XOR:
                if (!(leftChild.getType() instanceof IntType && (rightChild.getType() instanceof IntType))) throw new SemanticError(node.position(), "Invalid type for " + node.getOperator().name() + " operator");
                node.setType(IntType.getInstance());
                node.setLeftValue(false);
                break;
            case GREATER:
            case LESS:
            case GREATER_EQUAL:
            case LESS_EQUAL:
                if (!(leftChild.getType() instanceof IntType || leftChild.getType() instanceof StringType)) throw new SemanticError(node.position(), "Invalid type for " + node.getOperator().name() + " operator");
                if (!(rightChild.getType() instanceof  IntType || rightChild.getType() instanceof StringType)) throw new SemanticError(node.position(), "Invalid type for " + node.getOperator().name() + " operator");
                if (!(leftChild.getType().equals(rightChild.getType()))) throw new SemanticError(node.position(), "Invalid type for " + node.getOperator().name() + " operator");
                node.setType(BoolType.getInstance());
                node.setLeftValue(false);
                break;
            case EQUAL:
            case INEQUAL:
                boolean bool;
                if (leftChild.getType() instanceof VoidType || rightChild.getType() instanceof VoidType) bool = true;
                else if (leftChild.getType() instanceof NullType) bool = !(rightChild.getType() instanceof ClassType || rightChild.getType() instanceof ArrayType);
                else if (rightChild.getType() instanceof NullType) bool = !(leftChild.getType() instanceof ClassType || leftChild.getType() instanceof ArrayType);
                else bool = !(leftChild.getType().equals(rightChild.getType()));
                if (bool) throw new SemanticError(node.position(), "Invalid type for " + node.getOperator().name() + " operator");
                node.setType(BoolType.getInstance());
                node.setLeftValue(false);
                break;
            case LOGIC_OR:
            case LOGIC_AND:
                if (!(leftChild.getType() instanceof BoolType)) throw new SemanticError(node.position(), "Invalid type for " + node.getOperator().name() + " operator");
                if (!(rightChild.getType() instanceof BoolType)) throw new SemanticError(node.position(), "Invalid type for " + node.getOperator().name() + " operator");
                node.setType(BoolType.getInstance());
                node.setLeftValue(false);
                break;
            default:
                throw new SemanticError(node.position(), "Invalid binary operator");
        }
    }

    @Override
    public void visit(AssignExpressionNode node) {
        boolean bool;
        ExpressionNode leftChild = node.getLeftChild();
        ExpressionNode rightChild = node.getRightChild();
        leftChild.accept(this);
        rightChild.accept(this);
        if (!(leftChild.isLeftValue())) throw new SemanticError(node.position(), "Invalid non-left-value type for assign expression");
        if (leftChild.getType() instanceof VoidType || rightChild.getType() instanceof VoidType) bool = true;
        else if (rightChild.getType() instanceof NullType) bool = !(leftChild.getType() instanceof ClassType || leftChild.getType() instanceof ArrayType);
        else bool = !leftChild.getType().equals(rightChild.getType());
        if (bool) throw new SemanticError(node.position(), "Invalid type for assign expression");
        node.setType(leftChild.getType());
        node.setLeftValue(false);
    }

    @Override
    public void visit(IdentifierExpressionNode node) {
        String name = node.getIdentifier();
        Entity entity = currentScope.getCheckNonClass(node.position(), name);
        if (entity instanceof VariableEntity) {
            node.setEntity((VariableEntity) entity);
            node.setLeftValue(true);
        } else if (entity instanceof FunctionEntity) {
            currentFunctionEntity = (FunctionEntity) entity;
            node.setLeftValue(false);
        }
        node.setType(entity.getType());
    }

    @Override
    public void visit(ThisExpressionNode node) {
        Entity entity = currentScope.getCheckNonClass(node.position(), Scope.THIS_PARAMETERS_NAME);
        node.setType(entity.getType());
        node.setLeftValue(false);
    }

    @Override
    public void visit(IntConstantExpressionNode node) {
        node.setType(IntType.getInstance());
        node.setLeftValue(false);
    }

    @Override
    public void visit(BoolConstantExpressionNode node) {
        node.setType(BoolType.getInstance());
        node.setLeftValue(false);
    }

    @Override
    public void visit(StringConstantExpressionNode node) {
        node.setType(StringType.getInstance());
        node.setLeftValue(false);
    }

    @Override
    public void visit(NullExpressionNode node) {
        node.setType(NullType.getInstance());
        node.setLeftValue(false);
    }
}
