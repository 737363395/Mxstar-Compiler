package cn.cyx666.Mxstar.IR;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.DeclarationNode.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;
import cn.cyx666.Mxstar.AST.ProgramNode.*;
import cn.cyx666.Mxstar.AST.StatementNode.*;
import cn.cyx666.Mxstar.Configuration.*;
import cn.cyx666.Mxstar.IR.BasicBlock.*;
import cn.cyx666.Mxstar.IR.Data.*;
import cn.cyx666.Mxstar.IR.Function.*;
import cn.cyx666.Mxstar.IR.HyperBlock.*;
import cn.cyx666.Mxstar.IR.Instruction.*;
import cn.cyx666.Mxstar.Scope.Entity.*;
import cn.cyx666.Mxstar.Scope.Scanner.*;
import cn.cyx666.Mxstar.Scope.*;
import cn.cyx666.Mxstar.Type.*;

import java.util.*;

public class IRBuilder extends ScopeScanner {
    private IR ir;
    private IRFunction currentFunction = null;
    private BasicBlock currentBasicBlock = null;
    private Scope globalScope, currentScope;
    private Map<String, ExpressionNode> globalVariableMap = new HashMap<>();
    private List<String> globalVariableList = new ArrayList<>();
    private boolean isParameter = false;
    private Stack<Boolean> needAddress = new Stack<>();
    private Stack<BasicBlock> stepBasicBlockStack = new Stack<>(), nextBasicBlockStack = new Stack<>();
    private String currentClassName = null;
    private boolean uselessConstant = false;
    private Set<IdentifierExpressionNode> identifierSet = new HashSet<>();

    private static String GLOBAL_INITIALIZATION_FUNCTION = "global_init";

    public IR getIr() {
        return ir;
    }

    public IRBuilder(Scope globalScope) {
        ir = new IR();
        this.globalScope = globalScope;
    }

    private FunctionDeclarationNode GlobalVariableInitialization(){
        List<ASTNode> astNodeList = new ArrayList<>();
        for (String name : globalVariableList) {
            IdentifierExpressionNode identifierExpressionNode = new IdentifierExpressionNode(name, null);
            VariableEntity variableEntity = (VariableEntity) globalScope.get(Scope.variableKey(name));
            identifierExpressionNode.setEntity(variableEntity);
            AssignExpressionNode assignExpressionNode = new AssignExpressionNode(identifierExpressionNode, globalVariableMap.get(name), null);
            astNodeList.add(new ExpressionStatementNode(assignExpressionNode, null));
        }
        BlockStatementNode blockStatementNode = new BlockStatementNode(astNodeList, null);
        blockStatementNode.initializeScope(globalScope);
        TypeExpressionNode typeExpressionNode = new TypeExpressionNode(VoidType.getInstance(), null);
        FunctionDeclarationNode functionDeclarationNode = new FunctionDeclarationNode(typeExpressionNode, GLOBAL_INITIALIZATION_FUNCTION, new ArrayList<>(), blockStatementNode, null);
        FunctionEntity functionEntity = new FunctionEntity(functionDeclarationNode);
        globalScope.put(Scope.functionKey(GLOBAL_INITIALIZATION_FUNCTION), functionEntity);
        IRFunction irFunction = new IRFunction(functionEntity);
        ir.addFunction(irFunction);
        return functionDeclarationNode;
    }

    @Override
    public void visit(ProgramNode node) {
        currentScope = globalScope;
        needAddress.push(false);
        for (DeclarationNode declarationNode : node.getDeclarations()) {
            if (declarationNode instanceof FunctionDeclarationNode) {
                FunctionEntity functionEntity = (FunctionEntity) currentScope.get(Scope.functionKey(declarationNode.getName()));
                ir.addFunction(new IRFunction(functionEntity));
            } else if (declarationNode instanceof ClassDeclarationNode) {
                ClassEntity entity = (ClassEntity) currentScope.get(Scope.classKey(declarationNode.getName()));
                currentScope = entity.getScope();
                for (FunctionDeclarationNode function: ((ClassDeclarationNode) declarationNode).getFunctions()) {
                    FunctionEntity functionEntity = (FunctionEntity) currentScope.get(Scope.functionKey(function.getName()));
                    ir.addFunction(new IRFunction(functionEntity));
                }
                currentScope = currentScope.getParent();
            } else if (declarationNode instanceof VariableDeclarationNode) {
                declarationNode.accept(this);
            }
        }
        FunctionDeclarationNode globalInitialization = GlobalVariableInitialization();
        globalInitialization.accept(this);
        for (DeclarationNode declaration: node.getDeclarations()) {
            if (!(declaration instanceof VariableDeclarationNode)){
                declaration.accept(this);
            }
        }
        for (IRFunction irFunction : ir.getFunctionMap().values()) {
            irFunction.updateCalleeSet();
        }
        ir.updateCalleeSet();
        needAddress.pop();
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        currentClassName = node.getName();
        currentScope = globalScope;
        for (FunctionDeclarationNode declarationNode : node.getFunctions()) {
            declarationNode.accept(this);
        }
        currentClassName = null;
    }

    private void IRAssign(Data destination, int addressOffset, ExpressionNode data, boolean needMemory) {
        if (data.getTrueBasicBlock() != null) {
            BasicBlock basicBlock = new BasicBlock(currentFunction, null);
            if (needMemory) {
                data.getTrueBasicBlock().addInstruction(new StoreInstruction(data.getTrueBasicBlock(), new ConstantInt(1), Configuration.getRegisterSize(), destination, addressOffset));
                data.getFalseBasicBlock().addInstruction(new StoreInstruction(data.getFalseBasicBlock(), new ConstantInt(0), Configuration.getRegisterSize(), destination, addressOffset));
            } else {
                data.getTrueBasicBlock().addInstruction(new MoveInstruction(data.getTrueBasicBlock(), (VirtualRegister) destination, new ConstantInt(1)));
                data.getFalseBasicBlock().addInstruction(new MoveInstruction(data.getFalseBasicBlock(), (VirtualRegister) destination, new ConstantInt(0)));
            }
            if (!data.getTrueBasicBlock().isJump()) data.getTrueBasicBlock().setJumpInstruction(new GotoInstruction(data.getTrueBasicBlock(), basicBlock));
            if (!data.getFalseBasicBlock().isJump()) data.getFalseBasicBlock().setJumpInstruction(new GotoInstruction(data.getFalseBasicBlock(), basicBlock));
            currentBasicBlock = basicBlock;
        } else {
            if (needMemory) {
                currentBasicBlock.addInstruction(new StoreInstruction(currentBasicBlock, data.getData(), Configuration.getRegisterSize(), destination, addressOffset));
            } else {
                currentBasicBlock.addInstruction(new MoveInstruction(currentBasicBlock, (Register) destination, data.getData()));
            }
        }
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        VariableEntity variableEntity = (VariableEntity) currentScope.get(Scope.variableKey(node.getName()));
        if (!variableEntity.isUsed()) return;
        if (currentScope.isGlobal()) {
            ConstantData constantData = new ConstantVariable(node.getName(), Configuration.getRegisterSize());
            ir.addConstantData(constantData);
            variableEntity.setRegister(constantData);
            if (node.getInitialization() != null) {
                globalVariableMap.put(node.getName(), node.getInitialization());
                globalVariableList.add(node.getName());
            }
        } else {
            VirtualRegister virtualRegister = new VirtualRegister(node.getName());
            variableEntity.setRegister(virtualRegister);
            if (isParameter) {
                currentFunction.addParameter(virtualRegister);
            }
            if (node.getInitialization() == null) {
                if (!isParameter) {
                    currentBasicBlock.addInstruction(new MoveInstruction(currentBasicBlock, virtualRegister, new ConstantInt(0)));
                }
            } else {
                if (node.getInitialization().getType() instanceof BoolType && !(node.getInitialization() instanceof BoolConstantExpressionNode)) {
                    node.getInitialization().setTrueBasicBlock(new BasicBlock(currentFunction, null));
                    node.getInitialization().setFalseBasicBlock(new BasicBlock(currentFunction, null));
                }
                node.getInitialization().accept(this);
                IRAssign(virtualRegister, 0, node.getInitialization(), false);
            }
        }
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        String functionName = currentClassName == null ? node.getName() :IRFunction.IRFunctionName(currentClassName, node.getName());
        currentFunction = ir.getFunction(functionName);
        currentBasicBlock = currentFunction.initializeFirstBasicBlock();
        currentScope = node.getBlock().getScope();
        if (currentClassName != null) {
            VariableEntity variableEntity = (VariableEntity) currentScope.get(Scope.variableKey(Scope.THIS_PARAMETERS_NAME));
            VirtualRegister virtualRegister = new VirtualRegister(Scope.THIS_PARAMETERS_NAME);
            variableEntity.setRegister(virtualRegister);
            currentFunction.addParameter(virtualRegister);
        }
        isParameter = true;
        for (VariableDeclarationNode parameter: node.getParameters()) {
            parameter.accept(this);
        }
        isParameter = false;
        currentScope = currentScope.getParent();
        if (node.getName().equals("main")) {
            currentBasicBlock.addInstruction(new FunctionCallInstruction(currentBasicBlock, ir.getFunction(GLOBAL_INITIALIZATION_FUNCTION), new ArrayList<>(), null));
        }
        node.getBlock().accept(this);
        if (!currentBasicBlock.isJump()) {
            if (node.getReturnType() == null || node.getReturnType().getType() instanceof VoidType) {
                currentBasicBlock.setJumpInstruction(new ReturnInstruction(currentBasicBlock, null));
            } else {
                currentBasicBlock.setJumpInstruction(new ReturnInstruction(currentBasicBlock, new ConstantInt(0)));
            }
        }
        if (currentFunction.getReturnList().size() > 1) {
            BasicBlock endBasicBlock = new BasicBlock(currentFunction, currentFunction.getName() + "_end");
            VirtualRegister virtualRegister = node.getReturnType() == null || node.getReturnType().getType() instanceof VoidType ? null : new VirtualRegister("returnData");
            List<ReturnInstruction> returnInstructionList = new ArrayList<>(currentFunction.getReturnList());
            for (ReturnInstruction returnInstruction : returnInstructionList) {
                BasicBlock basicBlock = returnInstruction.getParentBasicBlock();
                if (returnInstruction.getData() != null) {
                    returnInstruction.addFirst(new MoveInstruction(basicBlock, virtualRegister, returnInstruction.getData()));
                }
                returnInstruction.remove();
                basicBlock.setJumpInstruction(new GotoInstruction(basicBlock, endBasicBlock));
            }
            endBasicBlock.setJumpInstruction(new ReturnInstruction(endBasicBlock, virtualRegister));
            currentFunction.setEndBasicBlock(endBasicBlock);
        } else {
            currentFunction.setEndBasicBlock(currentFunction.getReturnList().get(0).getParentBasicBlock());
        }
        currentFunction = null;
    }

    @Override
    public void visit(BlockStatementNode node) {
        currentScope = node.getScope();
        for (ASTNode statement: node.getStatements()) {
            statement.accept(this);
            if (currentBasicBlock.isJump()) break;
        }
        currentScope = currentScope.getParent();
    }

    @Override
    public void visit(ExpressionStatementNode node) {
        node.getExpression().accept(this);
    }

    @Override
    public void visit(ConditionStatementNode node) {
        BasicBlock thenBasicBlock = new BasicBlock(currentFunction, "if_then");
        BasicBlock elseBasicBlock = node.getElseStatement() == null ? null : new BasicBlock(currentFunction, "if_else");
        BasicBlock nextBasicBlock = new BasicBlock(currentFunction, "if_next");
        node.getCondition().setTrueBasicBlock(thenBasicBlock);
        node.getCondition().setFalseBasicBlock(elseBasicBlock == null? nextBasicBlock : elseBasicBlock);
        node.getCondition().accept(this);
        if (node.getCondition() instanceof BoolConstantExpressionNode) {
            currentBasicBlock.addInstruction(new BranchInstruction(currentBasicBlock, node.getCondition().getData(), thenBasicBlock, elseBasicBlock));
        }
        currentBasicBlock = thenBasicBlock;
        node.getThenStatement().accept(this);
        if (!currentBasicBlock.isJump()) {
            currentBasicBlock.setJumpInstruction(new GotoInstruction(currentBasicBlock, nextBasicBlock));
        }
        if (elseBasicBlock != null) {
            currentBasicBlock = elseBasicBlock;
            node.getElseStatement().accept(this);
            if (!currentBasicBlock.isJump()) {
                currentBasicBlock.setJumpInstruction(new GotoInstruction(currentBasicBlock, nextBasicBlock));
            }
        }
        currentBasicBlock = nextBasicBlock;
    }

    @Override
    public void visit(WhileStatementNode node) {
        BasicBlock conditionBasicBlock = new BasicBlock(currentFunction, "while_condition");
        BasicBlock blockBasicBlock = new BasicBlock(currentFunction, "while_block");
        BasicBlock nextBasicBlock = new BasicBlock(currentFunction, "while_next");
        stepBasicBlockStack.push(conditionBasicBlock);
        nextBasicBlockStack.push(nextBasicBlock);
        currentBasicBlock.setJumpInstruction(new GotoInstruction(currentBasicBlock, conditionBasicBlock));
        currentBasicBlock = conditionBasicBlock;
        node.getCondition().setTrueBasicBlock(blockBasicBlock);
        node.getCondition().setFalseBasicBlock(nextBasicBlock);
        node.getCondition().accept(this);
        if (node.getCondition() instanceof BoolConstantExpressionNode) {
            currentBasicBlock.setJumpInstruction(new BranchInstruction(currentBasicBlock, node.getCondition().getData(), node.getCondition().getTrueBasicBlock(), node.getCondition().getFalseBasicBlock()));
        }
        currentBasicBlock = blockBasicBlock;
        node.getStatement().accept(this);
        if (!currentBasicBlock.isJump()) {
            currentBasicBlock.setJumpInstruction(new GotoInstruction(currentBasicBlock, conditionBasicBlock));
        }
        stepBasicBlockStack.pop();
        nextBasicBlockStack.pop();
        currentBasicBlock = nextBasicBlock;
    }

    @Override
    public void visit(ForStatementNode node) {
        BasicBlock blockBasicBlock = new BasicBlock(currentFunction, "for_block");
        BasicBlock nextBasicBlock = new BasicBlock(currentFunction, "for_next");
        BasicBlock conditionBasicBlock = node.getCondition() == null? blockBasicBlock : new BasicBlock(currentFunction, "for_condition");
        BasicBlock stepBasicBlock = node.getStep() == null? conditionBasicBlock : new BasicBlock(currentFunction, "for_step");
        ForHyperBlock forHyperBlock = new ForHyperBlock(conditionBasicBlock, stepBasicBlock, blockBasicBlock, nextBasicBlock);
        ir.addForHyperBlock(forHyperBlock);
        currentFunction.getForHyperBlockSet().add(forHyperBlock);
        stepBasicBlockStack.push(stepBasicBlock);
        nextBasicBlockStack.push(nextBasicBlock);
        if (node.getInitialization() != null) {
            node.getInitialization().accept(this);
        }
        currentBasicBlock.setJumpInstruction(new GotoInstruction(currentBasicBlock, conditionBasicBlock));
        if (node.getCondition() != null) {
            currentBasicBlock = conditionBasicBlock;
            node.getCondition().setTrueBasicBlock(blockBasicBlock);
            node.getCondition().setFalseBasicBlock(nextBasicBlock);
            node.getCondition().accept(this);
            if (node.getCondition() instanceof BoolConstantExpressionNode) {
                currentBasicBlock.setJumpInstruction(new BranchInstruction(currentBasicBlock, node.getCondition().getData(), blockBasicBlock, nextBasicBlock));
            }
        }
        if (node.getStep() != null) {
            currentBasicBlock = stepBasicBlock;
            node.getStep().accept(this);
            currentBasicBlock.setJumpInstruction(new GotoInstruction(currentBasicBlock, conditionBasicBlock));
        }
        currentBasicBlock = blockBasicBlock;
        if (node.getStatements() != null) {
            node.getStatements().accept(this);
        }
        if (!currentBasicBlock.isJump()) {
            currentBasicBlock.setJumpInstruction(new GotoInstruction(currentBasicBlock, stepBasicBlock));
        }
        stepBasicBlockStack.pop();
        nextBasicBlockStack.pop();
        currentBasicBlock = nextBasicBlock;
    }

    @Override
    public void visit(BreakStatementNode node) {
        currentBasicBlock.setJumpInstruction(new GotoInstruction(currentBasicBlock, nextBasicBlockStack.peek()));
    }

    @Override
    public void visit(ContinueStatementNode node) {
        currentBasicBlock.setJumpInstruction(new GotoInstruction(currentBasicBlock, stepBasicBlockStack.peek()));
    }

    @Override
    public void visit(ReturnStatementNode node) {
        Type returnType = currentFunction.getEntity().getReturnType();
        if (returnType == null || returnType instanceof VoidType) {
            currentBasicBlock.setJumpInstruction(new ReturnInstruction(currentBasicBlock, null));
        } else {
            if (returnType instanceof BoolType && !(node.getExpression() instanceof BoolConstantExpressionNode)) {
                node.getExpression().setTrueBasicBlock(new BasicBlock(currentFunction, null));
                node.getExpression().setFalseBasicBlock(new BasicBlock(currentFunction, null));
                node.getExpression().accept(this);
                VirtualRegister virtualRegister = new VirtualRegister("returnBoolData");
                IRAssign(virtualRegister, 0, node.getExpression(), false);
                currentBasicBlock.setJumpInstruction(new ReturnInstruction(currentBasicBlock, virtualRegister));
            } else {
                node.getExpression().accept(this);
                currentBasicBlock.setJumpInstruction(new ReturnInstruction(currentBasicBlock, node.getExpression().getData()));
            }
        }
    }

    private boolean needMemory(ExpressionNode node) {
        if (node instanceof SubscriptExpressionNode || node instanceof MemberAccessExpressionNode) return true;
        if (node instanceof IdentifierExpressionNode) {
            if (!identifierSet.contains(node)) {
                if (currentClassName != null) {
                    VariableEntity variableEntity = (VariableEntity) currentScope.get(Scope.variableKey(((IdentifierExpressionNode) node).getIdentifier()));
                    ((IdentifierExpressionNode) node).setNeedMemory(variableEntity.getRegister() == null);
                } else {
                    ((IdentifierExpressionNode) node).setNeedMemory(false);
                }
                identifierSet.add((IdentifierExpressionNode) node);
            }
            return ((IdentifierExpressionNode) node).isNeedMemory();
        }
        return false;
    }

    private void selfOperation(ExpressionNode node){
        ExpressionNode expression;
        BinaryOperationInstruction.BinaryOperator operator;
        ConstantInt one = new ConstantInt(1);
        needAddress.push(false);
        if (node instanceof PrefixExpressionNode) {
            expression = ((PrefixExpressionNode) node).getExpression();
            operator = ((PrefixExpressionNode) node).getOperator() == PrefixExpressionNode.PrefixOperators.PREFIX_INC ?
                    BinaryOperationInstruction.BinaryOperator.ADD : BinaryOperationInstruction.BinaryOperator.SUB;
            expression.accept(this);
            node.setData(expression.getData());
        } else {
            expression = ((SuffixExpressionNode) node).getExpression();
            operator = ((SuffixExpressionNode) node).getOperator() == SuffixExpressionNode.SuffixOperators.SUFFIX_INC ?
                    BinaryOperationInstruction.BinaryOperator.ADD : BinaryOperationInstruction.BinaryOperator.SUB;
            expression.accept(this);
            VirtualRegister virtualRegister = new VirtualRegister();
            currentBasicBlock.addInstruction(new MoveInstruction(currentBasicBlock, virtualRegister, expression.getData()));
            node.setData(virtualRegister);
        }
        if (needMemory(expression)) {
            VirtualRegister virtualRegister = new VirtualRegister();
            needAddress.push(true);
            expression.accept(this);
            needAddress.pop();
            currentBasicBlock.addInstruction(new BinaryOperationInstruction(currentBasicBlock, virtualRegister, operator, expression.getData(), one));
            currentBasicBlock.addInstruction(new StoreInstruction(currentBasicBlock, virtualRegister, Configuration.getRegisterSize(), expression.getAddress(), expression.getAddressOffset()));
            if (node instanceof PrefixExpressionNode) {
                expression.setData(virtualRegister);
            }
        } else {
            currentBasicBlock.addInstruction(new BinaryOperationInstruction(currentBasicBlock, (Register) expression.getData(), operator, expression.getData(), one));
        }
        needAddress.pop();
    }

    @Override
    public void visit(SuffixExpressionNode node) {
        selfOperation(node);
    }

    @Override
    public void visit(PrefixExpressionNode node) {
        VirtualRegister virtualRegister = new VirtualRegister();
        switch (node.getOperator()) {
            case PREFIX_INC:
            case PREFIX_DEC:
                selfOperation(node);
                break;
            case POS:
                node.setData(node.getExpression().getData());
                break;
            case NEG:
                node.setData(virtualRegister);
                node.getExpression().accept(this);
                currentBasicBlock.addInstruction(new UnaryOperationInstruction(currentBasicBlock, virtualRegister, UnaryOperationInstruction.UnaryOperator.NEG, node.getExpression().getData()));
                break;
            case BITWISE_NOT:
                node.setData(virtualRegister);
                node.getExpression().accept(this);
                currentBasicBlock.addInstruction(new UnaryOperationInstruction(currentBasicBlock, virtualRegister, UnaryOperationInstruction.UnaryOperator.BITWISE_NOT, node.getExpression().getData()));
                break;
            case LOGIC_NOT:
                node.getExpression().setTrueBasicBlock(node.getFalseBasicBlock());
                node.getExpression().setFalseBasicBlock(node.getTrueBasicBlock());
                node.getExpression().accept(this);
                break;
        }
    }

    private void arrayDimensionExpand(NewExpressionNode node, VirtualRegister baseRegister, Data address, int index) {
        VirtualRegister virtualRegister = new VirtualRegister();
        ExpressionNode dimension = node.getDimensions().get(index);
        needAddress.push(false);
        dimension.accept(this);
        needAddress.pop();
        currentBasicBlock.addInstruction(new BinaryOperationInstruction(currentBasicBlock, virtualRegister, BinaryOperationInstruction.BinaryOperator.MUL, dimension.getData(), new ConstantInt(Configuration.getRegisterSize())));
        currentBasicBlock.addInstruction(new BinaryOperationInstruction(currentBasicBlock, virtualRegister, BinaryOperationInstruction.BinaryOperator.ADD, virtualRegister, new ConstantInt(Configuration.getRegisterSize())));
        currentBasicBlock.addInstruction(new HeapAllocateInstruction(currentBasicBlock, virtualRegister, virtualRegister));
        currentBasicBlock.addInstruction(new StoreInstruction(currentBasicBlock, dimension.getData(), Configuration.getRegisterSize(), virtualRegister, 0));
        if (index < node.getDimensions().size() - 1) {
            VirtualRegister loopIndex = new VirtualRegister();
            VirtualRegister currentAddress = new VirtualRegister();
            currentBasicBlock.addInstruction(new MoveInstruction(currentBasicBlock, loopIndex, new ConstantInt(0)));
            currentBasicBlock.addInstruction(new MoveInstruction(currentBasicBlock, currentAddress, virtualRegister));
            BasicBlock conditionBasicBlock = new BasicBlock(currentFunction, "conditionBasicBlock");
            BasicBlock blockBasicBlock = new BasicBlock(currentFunction, "blockBasicBlock");
            BasicBlock nextBasicBlock = new BasicBlock(currentFunction, "nextBasicBlock");
            currentBasicBlock.setJumpInstruction(new GotoInstruction(currentBasicBlock, conditionBasicBlock));
            currentBasicBlock = conditionBasicBlock;
            VirtualRegister comparision = new VirtualRegister();
            currentBasicBlock.addInstruction(new ComparisonInstruction(currentBasicBlock, comparision, ComparisonInstruction.ComparisionOperator.LESS, loopIndex, dimension.getData()));
            currentBasicBlock.setJumpInstruction(new BranchInstruction(currentBasicBlock, comparision, blockBasicBlock, nextBasicBlock));
            currentBasicBlock = blockBasicBlock;
            currentBasicBlock.addInstruction(new BinaryOperationInstruction(currentBasicBlock, currentAddress, BinaryOperationInstruction.BinaryOperator.ADD, currentAddress, new ConstantInt(Configuration.getRegisterSize())));
            arrayDimensionExpand(node, null, currentAddress, index + 1);
            currentBasicBlock.addInstruction(new BinaryOperationInstruction(currentBasicBlock, loopIndex, BinaryOperationInstruction.BinaryOperator.ADD, loopIndex, new ConstantInt(1)));
            currentBasicBlock.setJumpInstruction(new GotoInstruction(currentBasicBlock, conditionBasicBlock));
            currentBasicBlock = nextBasicBlock;
        }
        if (index == 0) {
            currentBasicBlock.addInstruction(new MoveInstruction(currentBasicBlock, baseRegister, virtualRegister));
        } else {
            currentBasicBlock.addInstruction(new StoreInstruction(currentBasicBlock, virtualRegister, Configuration.getRegisterSize(), address, 0));
        }
    }

    @Override
    public void visit(NewExpressionNode node) {
        VirtualRegister virtualRegister = new VirtualRegister();
        Type type = node.getNewType().getType();
        if (type instanceof ClassType) {
            String className = ((ClassType) type).getName();
            ClassEntity classEntity = (ClassEntity) globalScope.get(Scope.classKey(className));
            currentBasicBlock.addInstruction(new HeapAllocateInstruction(currentBasicBlock, virtualRegister, new ConstantInt(classEntity.getSize())));
            String functionName = IRFunction.IRFunctionName(className, className);
            IRFunction irFunction = ir.getFunction(functionName);
            if (irFunction != null) {
                List<Data> parameter = new ArrayList<>();
                parameter.add(virtualRegister);
                currentBasicBlock.addInstruction(new FunctionCallInstruction(currentBasicBlock, irFunction, parameter, null));
            }
        } else if (type instanceof ArrayType) {
            arrayDimensionExpand(node, virtualRegister, null, 0);
        }
        node.setData(virtualRegister);
    }

    private void logicalBinaryExpression(BinaryExpressionNode node) {
        if (node.getOperator() == BinaryExpressionNode.BinaryOperators.LOGIC_AND) {
            node.getLeftChild().setTrueBasicBlock(new BasicBlock(currentFunction, "andTrue"));
            node.getLeftChild().setFalseBasicBlock(node.getFalseBasicBlock());
            node.getLeftChild().accept(this);
            currentBasicBlock = node.getLeftChild().getTrueBasicBlock();
        } else {
            node.getLeftChild().setTrueBasicBlock(node.getTrueBasicBlock());
            node.getLeftChild().setFalseBasicBlock(new BasicBlock(currentFunction, "orFalse"));
            node.getLeftChild().accept(this);
            currentBasicBlock = node.getLeftChild().getFalseBasicBlock();
        }
        node.getRightChild().setTrueBasicBlock(node.getTrueBasicBlock());
        node.getRightChild().setFalseBasicBlock(node.getFalseBasicBlock());
        node.getRightChild().accept(this);
    }

    private void stringBinaryExpression(BinaryExpressionNode node) {
        IRFunction callee = null;
        List<Data> parameter =  new ArrayList<>();
        VirtualRegister virtualRegister = new VirtualRegister();
        node.getLeftChild().accept(this);
        node.getRightChild().accept(this);
        switch (node.getOperator()) {
            case ADD:
                callee = ir.getBuiltInFunction(IR.STRING_CONCATENATE_NAME);
                break;
            case EQUAL:
                callee = ir.getBuiltInFunction(IR.STRING_EQUAL_NAME);
                break;
            case INEQUAL:
                callee = ir.getBuiltInFunction(IR.STRING_INEQUAL_NAME);
                break;
            case GREATER:
                node.swap();
                callee = ir.getBuiltInFunction(IR.STRING_LESS_NAME);
                break;
            case GREATER_EQUAL:
                node.swap();
                callee = ir.getBuiltInFunction(IR.STRING_LESS_EQUAL_NAME);
                break;
            case LESS:
                callee = ir.getBuiltInFunction(IR.STRING_LESS_NAME);
                break;
            case LESS_EQUAL:
                callee = ir.getBuiltInFunction(IR.STRING_LESS_EQUAL_NAME);
                break;
        }
        parameter.add(node.getLeftChild().getData());
        parameter.add(node.getRightChild().getData());
        currentBasicBlock.addInstruction(new FunctionCallInstruction(currentBasicBlock, callee, parameter, virtualRegister));

        if (node.getTrueBasicBlock() != null) {
            currentBasicBlock.setJumpInstruction(new BranchInstruction(currentBasicBlock, virtualRegister, node.getTrueBasicBlock(), node.getFalseBasicBlock()));
        } else {
            node.setData(virtualRegister);
        }
    }

    private void arithmeticBinaryExpression(BinaryExpressionNode node) {
        if (node.getLeftChild().getType() instanceof StringType) {
            stringBinaryExpression(node);
            return;
        }
        node.getLeftChild().accept(this);
        node.getRightChild().accept(this);
        Data leftData = node.getLeftChild().getData(), rightData = node.getRightChild().getData();
        boolean isConstant = leftData instanceof ConstantInt && rightData instanceof ConstantInt;
        int leftConstant = leftData instanceof ConstantInt ? ((ConstantInt) leftData).getValue() : 0, rightConstant =  rightData instanceof ConstantInt ? ((ConstantInt) rightData).getValue() : 0;
        ComparisonInstruction.ComparisionOperator comparisionOperator = null;
        BinaryOperationInstruction.BinaryOperator binaryOperator = null;
        switch (node.getOperator()) {
            case MUL:
                binaryOperator = BinaryOperationInstruction.BinaryOperator.MUL;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant * rightConstant));
                    return;
                }
                break;
            case DIV:
                binaryOperator = BinaryOperationInstruction.BinaryOperator.DIV;
                if (isConstant && rightConstant != 0) {
                    node.setData(new ConstantInt(leftConstant / rightConstant));
                    return;
                }
                ir.setDivision(true);
                break;
            case MOD:
                binaryOperator = BinaryOperationInstruction.BinaryOperator.MOD;
                if (isConstant && rightConstant != 0) {
                    node.setData(new ConstantInt(leftConstant % rightConstant));
                    return;
                }
                ir.setDivision(true);
                break;
            case ADD:
                binaryOperator = BinaryOperationInstruction.BinaryOperator.ADD;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant + rightConstant));
                    return;
                }
                break;
            case SUB:
                binaryOperator = BinaryOperationInstruction.BinaryOperator.SUB;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant - rightConstant));
                    return;
                }
                break;
            case SHL:
                binaryOperator = BinaryOperationInstruction.BinaryOperator.SHL;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant << rightConstant));
                    return;
                }
                ir.setShift(true);
                break;
            case SHR:
                binaryOperator = BinaryOperationInstruction.BinaryOperator.SHR;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant >> rightConstant));
                    return;
                }
                ir.setShift(true);
                break;
            case BITWISE_AND:
                binaryOperator = BinaryOperationInstruction.BinaryOperator.BITWISE_AND;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant & rightConstant));
                    return;
                }
                break;
            case BITWISE_OR:
                binaryOperator = BinaryOperationInstruction.BinaryOperator.BITWISE_OR;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant | rightConstant));
                    return;
                }
                break;
            case BITWISE_XOR:
                binaryOperator = BinaryOperationInstruction.BinaryOperator.BITWISE_XOR;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant ^ rightConstant));
                    return;
                }
                break;
            case LESS:
                comparisionOperator = ComparisonInstruction.ComparisionOperator.LESS;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant < rightConstant ? 1 : 0));
                    return;
                }
                break;
            case GREATER:
                comparisionOperator = ComparisonInstruction.ComparisionOperator.GREATER;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant > rightConstant ? 1 : 0));
                    return;
                }
                break;
            case EQUAL:
                comparisionOperator = ComparisonInstruction.ComparisionOperator.EQUAL;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant == rightConstant ? 1 : 0));
                    return;
                }
                break;
            case INEQUAL:
                comparisionOperator = ComparisonInstruction.ComparisionOperator.INEQUAL;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant != rightConstant ? 1 : 0));
                    return;
                }
                break;
            case LESS_EQUAL:
                comparisionOperator = ComparisonInstruction.ComparisionOperator.LESS_EQUAL;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant <= rightConstant ? 1 : 0));
                    return;
                }
                break;
            case GREATER_EQUAL:
                comparisionOperator = ComparisonInstruction.ComparisionOperator.GREATER_EQUAL;
                if (isConstant) {
                    node.setData(new ConstantInt(leftConstant <= rightConstant ? 1 : 0));
                    return;
                }
                break;
        }
        if (binaryOperator != null) {
            VirtualRegister virtualRegister = new VirtualRegister();
            node.setData(virtualRegister);
            currentBasicBlock.addInstruction(new BinaryOperationInstruction(currentBasicBlock, virtualRegister, binaryOperator, leftData, rightData));
        } else {
            VirtualRegister virtualRegister = new VirtualRegister();
            currentBasicBlock.addInstruction(new ComparisonInstruction(currentBasicBlock, virtualRegister, comparisionOperator, leftData, rightData));
            if (leftData instanceof ConstantInt) {
                ((ComparisonInstruction)currentBasicBlock.getLastInstruction()).swap();
            }
            if (node.getTrueBasicBlock() != null) {
                currentBasicBlock.setJumpInstruction(new BranchInstruction(currentBasicBlock, virtualRegister, node.getTrueBasicBlock(), node.getFalseBasicBlock()));
            } else {
                node.setData(virtualRegister);
            }
        }
    }

    @Override
    public void visit(BinaryExpressionNode node) {
        switch (node.getOperator()) {
            case LOGIC_AND:
            case LOGIC_OR:
                logicalBinaryExpression(node);
                return;
            case MUL:
            case DIV:
            case MOD:
            case ADD:
            case SUB:
            case SHL:
            case SHR:
            case BITWISE_AND:
            case BITWISE_OR:
            case BITWISE_XOR:
            case GREATER:
            case LESS:
            case GREATER_EQUAL:
            case LESS_EQUAL:
            case EQUAL:
            case INEQUAL:
                arithmeticBinaryExpression(node);
        }
    }

    private void printFunctionCall(ExpressionNode node, String functionName) {
        if (node instanceof BinaryExpressionNode) {
            printFunctionCall(((BinaryExpressionNode) node).getLeftChild(), "print");
            printFunctionCall(((BinaryExpressionNode) node).getRightChild(), functionName);
        } else {
            IRFunction callee;
            List<Data> parameters = new ArrayList<>();
            if (node instanceof FunctionCallExpressionNode && ((FunctionCallExpressionNode) node).getEntity().getName().equals("toString")) {
                ExpressionNode expressionNode = ((FunctionCallExpressionNode) node).getParameters().get(0);
                expressionNode.accept(this);
                callee = ir.getBuiltInFunction(functionName + "Int");
                parameters.add(expressionNode.getData());
            } else {
                node.accept(this);
                callee = ir.getBuiltInFunction(functionName);
                parameters.add(node.getData());
            }
            currentBasicBlock.addInstruction(new FunctionCallInstruction(currentBasicBlock, callee, parameters, null));
        }
    }

    @Override
    public void visit(FunctionCallExpressionNode node) {
        FunctionEntity functionEntity = node.getEntity();
        String functionName = functionEntity.getName();
        List<Data> parameters = new ArrayList<>();
        ExpressionNode thisExpressionNode = null;
        if (functionEntity.isMember()) {
            if (node.getFunction() instanceof MemberAccessExpressionNode) {
                thisExpressionNode = ((MemberAccessExpressionNode) (node.getFunction())).getExpression();
            } else {
                thisExpressionNode = new ThisExpressionNode(null);
                thisExpressionNode.setType(new ClassType(currentClassName));
            }
            thisExpressionNode.accept(this);
            String className;
            if (thisExpressionNode.getType() instanceof ClassType) {
                className = ((ClassType) (thisExpressionNode.getType())).getName();
            } else if (thisExpressionNode.getType() instanceof ArrayType) {
                className = Scope.ARRAY_CLASS_NAME;
            } else {
                className = Scope.STRING_CLASS_NAME;
            }
            functionName = IRFunction.IRFunctionName(className, functionName);
            parameters.add(thisExpressionNode.getData());
        }
        if (functionEntity.isBuiltIn()) {
            VirtualRegister virtualRegister;
            List<Data> subParameters;
            needAddress.push(false);
            switch (functionName) {
                case IR.PRINT_NAME:
                case IR.PRINTLN_NAME:
                    printFunctionCall(node.getParameters().get(0), functionName);
                    break;
                case IR.GETSTRING_NAME:
                    virtualRegister = new VirtualRegister("getString");
                    currentBasicBlock.addInstruction(new FunctionCallInstruction(currentBasicBlock, ir.getBuiltInFunction(IR.GETSTRING_NAME), new ArrayList<Data>(), virtualRegister));
                    node.setData(virtualRegister);
                    break;
                case IR.GETINT_NAME:
                    virtualRegister = new VirtualRegister("getInt");
                    currentBasicBlock.addInstruction(new FunctionCallInstruction(currentBasicBlock, ir.getBuiltInFunction(IR.GETINT_NAME), new ArrayList<Data>(), virtualRegister));
                    node.setData(virtualRegister);
                    break;
                case IR.TOSTRING_NAME:
                    node.getParameters().get(0).accept(this);
                    virtualRegister = new VirtualRegister("toString");
                    subParameters = new ArrayList<>();
                    subParameters.add(node.getParameters().get(0).getData());
                    currentBasicBlock.addInstruction(new FunctionCallInstruction(currentBasicBlock, ir.getBuiltInFunction(IR.TOSTRING_NAME), subParameters, virtualRegister));
                    node.setData(virtualRegister);
                    break;
                case IR.STRING_SUBSTRING_NAME:
                    node.getParameters().get(0).accept(this);
                    node.getParameters().get(1).accept(this);
                    virtualRegister = new VirtualRegister("subString");
                    subParameters = new ArrayList<>();
                    subParameters.add(thisExpressionNode.getData());
                    subParameters.add(node.getParameters().get(0).getData());
                    subParameters.add(node.getParameters().get(1).getData());
                    currentBasicBlock.addInstruction(new FunctionCallInstruction(currentBasicBlock, ir.getBuiltInFunction(IR.STRING_SUBSTRING_NAME), subParameters, virtualRegister));
                    node.setData(virtualRegister);
                    break;
                case IR.STRING_PARSEINT_NAME:
                    virtualRegister = new VirtualRegister("parseInt");
                    subParameters = new ArrayList<>();
                    subParameters.add(thisExpressionNode.getData());
                    currentBasicBlock.addInstruction(new FunctionCallInstruction(currentBasicBlock, ir.getBuiltInFunction(IR.STRING_PARSEINT_NAME), subParameters, virtualRegister));
                    node.setData(virtualRegister);
                    break;
                case IR.STRING_ORD_NAME:
                    node.getParameters().get(0).accept(this);
                    virtualRegister = new VirtualRegister("ord");
                    subParameters = new ArrayList<>();
                    subParameters.add(thisExpressionNode.getData());
                    subParameters.add(node.getParameters().get(0).getData());
                    currentBasicBlock.addInstruction(new FunctionCallInstruction(currentBasicBlock, ir.getBuiltInFunction(IR.STRING_ORD_NAME), subParameters, virtualRegister));
                    node.setData(virtualRegister);
                    break;
                case IR.STRING_LENGTH_NAME:
                    virtualRegister = new VirtualRegister("length");
                    currentBasicBlock.addInstruction(new LoadInstruction(currentBasicBlock, virtualRegister, Configuration.getRegisterSize(), thisExpressionNode.getData(), 0));
                    node.setData(virtualRegister);
                    break;
                case IR.ARRAY_SIZE_NAME:
                    virtualRegister = new VirtualRegister("size");
                    currentBasicBlock.addInstruction(new LoadInstruction(currentBasicBlock, virtualRegister, Configuration.getRegisterSize(), thisExpressionNode.getData(), 0));
                    node.setData(virtualRegister);
                    break;
            }
            needAddress.pop();
            return;
        }
        for (ExpressionNode parameter : node.getParameters()) {
            parameter.accept(this);
            parameters.add(parameter.getData());
        }
        IRFunction irFunction = ir.getFunction(functionName);
        VirtualRegister virtualRegister = new VirtualRegister();
        currentBasicBlock.addInstruction(new FunctionCallInstruction(currentBasicBlock, irFunction, parameters, virtualRegister));
        node.setData(virtualRegister);
        if (node.getTrueBasicBlock() != null) {
            currentBasicBlock.setJumpInstruction(new BranchInstruction(currentBasicBlock, node.getData(), node.getTrueBasicBlock(), node.getFalseBasicBlock()));
        }
    }

    @Override
    public void visit(SubscriptExpressionNode node) {
        VirtualRegister virtualRegister = new VirtualRegister();
        ConstantInt elementSize = new ConstantInt(node.getType().getSize());
        needAddress.push(false);
        node.getArray().accept(this);
        if (uselessConstant) {
            needAddress.pop();
            return;
        }
        node.getSubscript().accept(this);
        needAddress.pop();
        currentBasicBlock.addInstruction(new BinaryOperationInstruction(currentBasicBlock, virtualRegister, BinaryOperationInstruction.BinaryOperator.MUL, node.getSubscript().getData(), elementSize));
        currentBasicBlock.addInstruction(new BinaryOperationInstruction(currentBasicBlock, virtualRegister, BinaryOperationInstruction.BinaryOperator.ADD, node.getArray().getData(), virtualRegister));
        if (needAddress.peek()) {
            node.setAddress(virtualRegister);
            node.setAddressOffset(Configuration.getRegisterSize());
        } else {
            currentBasicBlock.addInstruction(new LoadInstruction(currentBasicBlock, virtualRegister, node.getType().getSize(), virtualRegister, Configuration.getRegisterSize()));
            node.setData(virtualRegister);
            if (node.getTrueBasicBlock() != null) {
                currentBasicBlock.setJumpInstruction(new BranchInstruction(currentBasicBlock, node.getData(), node.getTrueBasicBlock(), node.getFalseBasicBlock()));
            }
        }
    }

    @Override
    public void visit(MemberAccessExpressionNode node) {
        Data classAddress;
        String className = ((ClassType)(node.getExpression().getType())).getName();
        ClassEntity classEntity = (ClassEntity) currentScope.get(Scope.classKey(className));
        VariableEntity variableEntity = (VariableEntity) classEntity.getScope().getLocal(Scope.variableKey(node.getMember()));
        needAddress.push(false);
        node.getExpression().accept(this);
        needAddress.pop();
        classAddress = node.getExpression().getData();
        if (needAddress.peek()) {
            node.setAddress(classAddress);
            node.setAddressOffset(variableEntity.getAddressOffset());
        } else {
            VirtualRegister virtualRegister = new VirtualRegister();
            node.setData(virtualRegister);
            currentBasicBlock.addInstruction(new LoadInstruction(currentBasicBlock, virtualRegister, variableEntity.getType().getSize(), classAddress, variableEntity.getAddressOffset()));
            if (node.getTrueBasicBlock() != null) {
                currentBasicBlock.setJumpInstruction(new BranchInstruction(currentBasicBlock, node.getData(), node.getTrueBasicBlock(), node.getFalseBasicBlock()));
            }
        }
    }

    @Override
    public void visit(AssignExpressionNode node) {
        boolean needMemory = needMemory(node.getLeftChild());
        Data destination;
        int addressOffset;
        needAddress.push(needMemory);
        uselessConstant = false;
        node.getLeftChild().accept(this);
        needAddress.pop();
        if (uselessConstant) {
            uselessConstant = false;
            return;
        }
        if (node.getRightChild().getType() instanceof BoolType && !(node.getRightChild() instanceof BoolConstantExpressionNode)) {
            node.getRightChild().setTrueBasicBlock(new BasicBlock(currentFunction, null));
            node.getRightChild().setFalseBasicBlock(new BasicBlock(currentFunction, null));
        }
        node.getRightChild().accept(this);
        if (needMemory) {
            destination = node.getLeftChild().getAddress();
            addressOffset = node.getLeftChild().getAddressOffset();
        } else {
            destination = node.getLeftChild().getData();
            addressOffset = 0;
        }
        IRAssign(destination, addressOffset, node.getRightChild(), needMemory);
        node.setData(node.getRightChild().getData());
    }

    @Override
    public void visit(IdentifierExpressionNode node) {
        VariableEntity variableEntity = node.getEntity();
        if ((variableEntity.getType() instanceof ArrayType || variableEntity.isGlobal()) && !variableEntity.isUsed()) {
            uselessConstant = true;
            return;
        }
        if (variableEntity.getRegister() == null) {
            ThisExpressionNode thisExpressionNode = new ThisExpressionNode(null);
            thisExpressionNode.setType(new ClassType(currentClassName));
            MemberAccessExpressionNode memberAccessExpressionNode = new MemberAccessExpressionNode(thisExpressionNode, node.getIdentifier(), null);
            memberAccessExpressionNode.accept(this);
            if (needAddress.peek()) {
                node.setAddress(memberAccessExpressionNode.getAddress());
                node.setAddressOffset(memberAccessExpressionNode.getAddressOffset());
            } else {
                node.setData(memberAccessExpressionNode.getData());
                if (node.getTrueBasicBlock() != null) {
                    currentBasicBlock.setJumpInstruction(new BranchInstruction(currentBasicBlock, node.getData(), node.getTrueBasicBlock(), node.getFalseBasicBlock()));
                }
            }
            node.setNeedMemory(true);
        }
        else {
            node.setData(variableEntity.getRegister());
            if (node.getTrueBasicBlock() != null) {
                currentBasicBlock.setJumpInstruction(new BranchInstruction(currentBasicBlock, node.getData(), node.getTrueBasicBlock(), node.getFalseBasicBlock()));
            }
        }
    }

    @Override
    public void visit(ThisExpressionNode node) {
        VariableEntity variableEntity = (VariableEntity) currentScope.get(Scope.variableKey(Scope.THIS_PARAMETERS_NAME));
        node.setData(variableEntity.getRegister());
        if (node.getTrueBasicBlock() != null) {
            currentBasicBlock.setJumpInstruction(new BranchInstruction(currentBasicBlock, node.getData(), node.getTrueBasicBlock(), node.getFalseBasicBlock()));
        }
    }

    @Override
    public void visit(IntConstantExpressionNode node) {
        node.setData(new ConstantInt(node.getValue()));
    }

    @Override
    public void visit(StringConstantExpressionNode node) {
        ConstantString constantString = ir.getConstantString(node.getValue());
        if (constantString == null) {
            constantString = new ConstantString(node.getValue());
            ir.addConstantString(constantString);
        }
        node.setData(constantString);
    }

    @Override
    public void visit(BoolConstantExpressionNode node) {
        node.setData(new ConstantInt(node.getValue() ? 1 : 0));
    }

    @Override
    public void visit(NullExpressionNode node) {
        node.setData(new ConstantInt(0));
    }
}
