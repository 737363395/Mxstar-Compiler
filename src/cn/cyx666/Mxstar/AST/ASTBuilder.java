package cn.cyx666.Mxstar.AST;

import cn.cyx666.Mxstar.AST.ProgramNode.*;
import cn.cyx666.Mxstar.AST.StatementNode.*;
import cn.cyx666.Mxstar.Error.SemanticError;
import cn.cyx666.Mxstar.Parser.*;
import cn.cyx666.Mxstar.AST.DeclarationNode.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;
import cn.cyx666.Mxstar.Type.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilder extends MxstarBaseVisitor<ASTNode> {

    private TypeExpressionNode lastType;

    @Override
    public ASTNode visitProgram(MxstarParser.ProgramContext ctx) {
        List<DeclarationNode> declarations = new ArrayList<>();
        if (ctx.declaration() != null) {
            for (ParserRuleContext declaration: ctx.declaration()) {
                ASTNode node = visit(declaration);
                if (node instanceof VariableListExpressionNode)declarations.addAll(((VariableListExpressionNode) node).getDeclarations());
                else declarations.add((DeclarationNode) node);
            }
        }
        return new ProgramNode(declarations, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitDeclaration(MxstarParser.DeclarationContext ctx) {
        if (ctx.functionDeclaration() != null) return visit(ctx.functionDeclaration());
        else if (ctx.classDeclaration() != null) return visit(ctx.classDeclaration());
        else return visit(ctx.variableDeclaration());
    }

    @Override
    public ASTNode visitClassDeclaration(MxstarParser.ClassDeclarationContext ctx) {
        String name = ctx.Identifier().getText();
        List<VariableDeclarationNode> variables = new ArrayList<>();
        List<FunctionDeclarationNode> functions = new ArrayList<>();
        if (ctx.memberDeclaration() != null) {
            for (ParserRuleContext declaration : ctx.memberDeclaration()){
                ASTNode node = visit(declaration);
                if (node instanceof VariableListExpressionNode) variables.addAll(((VariableListExpressionNode) node).getDeclarations());
                else if (node instanceof FunctionDeclarationNode) functions.add((FunctionDeclarationNode) node);
            }
        }
        return new ClassDeclarationNode(name, functions, variables, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitFunctionDeclaration(MxstarParser.FunctionDeclarationContext ctx) {
        TypeExpressionNode type = ctx.typeOrVoid() == null ? null : (TypeExpressionNode) visit(ctx.typeOrVoid());
        String name = ctx.Identifier().getText();
        List<VariableDeclarationNode> parameters = new ArrayList<>();
        if (ctx.parameterDeclarationList() != null) {
            for (ParserRuleContext parameter : ctx.parameterDeclarationList().parameterDeclaration()) {
                parameters.add((VariableDeclarationNode) visit(parameter));
            }
        }
        BlockStatementNode block = (BlockStatementNode) visit(ctx.blockStatement());
        return new FunctionDeclarationNode(type, name, parameters, block, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitVariableDeclaration(MxstarParser.VariableDeclarationContext ctx) {
        lastType = (TypeExpressionNode) visit(ctx.type());
        return visit(ctx.variableList());
    }

    @Override
    public ASTNode visitVariableList(MxstarParser.VariableListContext ctx) {
        List<VariableDeclarationNode> variables = new ArrayList<>();
        for (ParserRuleContext variable : ctx.variable()) {
            variables.add((VariableDeclarationNode) visit(variable));
        }
        return new VariableListExpressionNode(variables);
    }

    @Override
    public ASTNode visitVariable(MxstarParser.VariableContext ctx) {
        String name = ctx.Identifier().getText();
        ExpressionNode initialization = ctx.expression() == null ? null : (ExpressionNode) visit(ctx.expression());
        return new VariableDeclarationNode(lastType, name, initialization, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitMemberDeclaration(MxstarParser.MemberDeclarationContext ctx) {
        if (ctx.functionDeclaration() != null) return visit(ctx.functionDeclaration());
        else return visit(ctx.variableDeclaration());
    }

    @Override
    public ASTNode visitParameterDeclaration(MxstarParser.ParameterDeclarationContext ctx) {
        TypeExpressionNode type = (TypeExpressionNode) visit(ctx.type());
        String name = ctx.Identifier().getText();
        return new VariableDeclarationNode(type, name, null, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitTypeOrVoid(MxstarParser.TypeOrVoidContext ctx) {
        if (ctx.type() != null) return visit(ctx.type());
        else return new TypeExpressionNode(VoidType.getInstance(), Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitArray(MxstarParser.ArrayContext ctx) {
        TypeExpressionNode type = (TypeExpressionNode) visit(ctx.type());
        return new TypeExpressionNode(new ArrayType(type.getType()), Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitNonArray(MxstarParser.NonArrayContext ctx) {
        return visit(ctx.nonArrayType());
    }

    @Override
    public ASTNode visitNonArrayType(MxstarParser.NonArrayTypeContext ctx) {
        if (ctx.Identifier() != null) return new TypeExpressionNode(new ClassType(ctx.Identifier().getText()), Position.fromContext(ctx));
        else if (ctx.Int() != null) return new TypeExpressionNode(IntType.getInstance(), Position.fromContext(ctx));
        else if (ctx.String() != null) return new TypeExpressionNode(StringType.getInstance(), Position.fromContext(ctx));
        else return new TypeExpressionNode(BoolType.getInstance(), Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitNonArrayTypeCreator(MxstarParser.NonArrayTypeCreatorContext ctx) {
        if (ctx.Identifier() != null) return new TypeExpressionNode(new ClassType(ctx.Identifier().getText()), Position.fromContext(ctx));
        else if (ctx.Int() != null) return new TypeExpressionNode(IntType.getInstance(), Position.fromContext(ctx));
        else if (ctx.String() != null) return new TypeExpressionNode(StringType.getInstance(), Position.fromContext(ctx));
        else return new TypeExpressionNode(BoolType.getInstance(), Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitBlockStatement(MxstarParser.BlockStatementContext ctx) {
        List<ASTNode> statements = new ArrayList<>();
        if (ctx.localStatement() != null) {
            for (ParserRuleContext statement : ctx.localStatement()) {
                ASTNode node = visit(statement);
                if (node != null) {
                    if (node instanceof VariableListExpressionNode) {
                        statements.addAll(((VariableListExpressionNode) node).getDeclarations());
                    } else statements.add(node);
                }
            }
        }
        return new BlockStatementNode(statements, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitBlock(MxstarParser.BlockContext ctx) {
        return visit(ctx.blockStatement());
    }

    @Override
    public ASTNode visitOther(MxstarParser.OtherContext ctx) {
        return visit(ctx.otherStatement());
    }

    @Override
    public ASTNode visitOtherStatement(MxstarParser.OtherStatementContext ctx) {
        return new ExpressionStatementNode((ExpressionNode)visit(ctx.expression()), Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitLoop(MxstarParser.LoopContext ctx) {
        return visit(ctx.loopStatement());
    }

    @Override
    public ASTNode visitCondition(MxstarParser.ConditionContext ctx) {
        return visit(ctx.conditionStatement());
    }

    @Override
    public ASTNode visitJump(MxstarParser.JumpContext ctx) {
        return visit(ctx.jumpStatement());
    }

    @Override
    public ASTNode visitConditionStatement(MxstarParser.ConditionStatementContext ctx) {
        ExpressionNode condition = (ExpressionNode) visit(ctx.expression());
        StatementNode thenStatement = (StatementNode) visit(ctx.thenStatement);
        StatementNode elseStatement = ctx.elseStatement == null ? null : (StatementNode) visit(ctx.elseStatement);
        return new ConditionStatementNode(condition, thenStatement, elseStatement, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitBlank(MxstarParser.BlankContext ctx) {
        return null;
    }

    @Override
    public ASTNode visitNonVariableStatement(MxstarParser.NonVariableStatementContext ctx) {
        return visit(ctx.statement());
    }

    @Override
    public ASTNode visitVariableStatement(MxstarParser.VariableStatementContext ctx) {
        return visit(ctx.variableDeclaration());
    }

    @Override
    public ASTNode visitWhile(MxstarParser.WhileContext ctx) {
        ExpressionNode condition = ctx.expression() == null ? null : (ExpressionNode) visit(ctx.expression());
        StatementNode statement = (StatementNode) visit(ctx.statement());
        return new WhileStatementNode(condition, statement, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitFor(MxstarParser.ForContext ctx) {
        ExpressionNode initialization = ctx.initialization == null ? null : (ExpressionNode) visit(ctx.initialization);
        ExpressionNode condition = ctx.condition == null ? null : (ExpressionNode) visit(ctx.condition);
        ExpressionNode step = ctx.step == null ? null : (ExpressionNode) visit(ctx.step);
        StatementNode statement = (StatementNode) visit(ctx.statement());
        return new ForStatementNode(initialization, condition, step, statement, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitContinue(MxstarParser.ContinueContext ctx) {
        return new ContinueStatementNode(Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitBreak(MxstarParser.BreakContext ctx) {
        return new BreakStatementNode(Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitReturn(MxstarParser.ReturnContext ctx) {
        ExpressionNode expression = ctx.expression() == null ? null : (ExpressionNode) visit(ctx.expression());
        return new ReturnStatementNode(expression, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitNewExpression(MxstarParser.NewExpressionContext ctx) {
        return visit(ctx.creator());
    }

    @Override
    public ASTNode visitPrefixExpression(MxstarParser.PrefixExpressionContext ctx) {
        PrefixExpressionNode.PrefixOperators operator = null;
        switch (ctx.operator.getText()) {
            case "++" :
                operator = PrefixExpressionNode.PrefixOperators.PREFIX_INC;
                break;
            case "--" :
                operator = PrefixExpressionNode.PrefixOperators.PREFIX_DEC;
                break;
            case "+" :
                operator = PrefixExpressionNode.PrefixOperators.POS;
                break;
            case "-" :
                operator = PrefixExpressionNode.PrefixOperators.NEG;
                break;
            case "!" :
                operator = PrefixExpressionNode.PrefixOperators.LOGIC_NOT;
                break;
            case "~" :
                operator = PrefixExpressionNode.PrefixOperators.BITWISE_NOT;
                break;
        }
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
        return new PrefixExpressionNode(operator, expression, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitSuffixExpression(MxstarParser.SuffixExpressionContext ctx) {
        SuffixExpressionNode.SuffixOperators operator = null;
        switch (ctx.operator.getText()) {
            case "++" :
                operator = SuffixExpressionNode.SuffixOperators.SUFFIX_INC;
                break;
            case "--" :
                operator = SuffixExpressionNode.SuffixOperators.SUFFIX_DEC;
                break;
        }
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
        return new SuffixExpressionNode(operator, expression, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitBinaryExpression(MxstarParser.BinaryExpressionContext ctx) {
        BinaryExpressionNode.BinaryOperators operator = null;
        switch (ctx.operator.getText()) {
            case "*"  :
                operator = BinaryExpressionNode.BinaryOperators.MUL;
                break;
            case "/"  :
                operator = BinaryExpressionNode.BinaryOperators.DIV;
                break;
            case "%"  :
                operator = BinaryExpressionNode.BinaryOperators.MOD;
                break;
            case "+"  :
                operator = BinaryExpressionNode.BinaryOperators.ADD;
                break;
            case "-"  :
                operator = BinaryExpressionNode.BinaryOperators.SUB;
                break;
            case "<<" :
                operator = BinaryExpressionNode.BinaryOperators.SHL;
                break;
            case ">>" :
                operator = BinaryExpressionNode.BinaryOperators.SHR;
                break;
            case "<"  :
                operator = BinaryExpressionNode.BinaryOperators.LESS;
                break;
            case ">"  :
                operator = BinaryExpressionNode.BinaryOperators.GREATER;
                break;
            case "<=" :
                operator = BinaryExpressionNode.BinaryOperators.LESS_EQUAL;
                break;
            case ">=" :
                operator = BinaryExpressionNode.BinaryOperators.GREATER_EQUAL;
                break;
            case "==" :
                operator = BinaryExpressionNode.BinaryOperators.EQUAL;
                break;
            case "!=" :
                operator = BinaryExpressionNode.BinaryOperators.INEQUAL;
                break;
            case "&"  :
                operator = BinaryExpressionNode.BinaryOperators.BITWISE_AND;
                break;
            case "^"  :
                operator = BinaryExpressionNode.BinaryOperators.BITWISE_XOR;
                break;
            case "|"  :
                operator = BinaryExpressionNode.BinaryOperators.BITWISE_OR;
                break;
            case "&&" :
                operator = BinaryExpressionNode.BinaryOperators.LOGIC_AND;
                break;
            case "||" :
                operator = BinaryExpressionNode.BinaryOperators.LOGIC_OR;
                break;
        }
        ExpressionNode leftChild = (ExpressionNode) visit(ctx.leftChild);
        ExpressionNode rightChild = (ExpressionNode) visit(ctx.rightChild);
        return new BinaryExpressionNode(operator, leftChild, rightChild, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitMemberAccessExpression(MxstarParser.MemberAccessExpressionContext ctx) {
        ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
        String member = ctx.Identifier().getText();
        return new MemberAccessExpressionNode(expression, member, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitPrimaryExpression(MxstarParser.PrimaryExpressionContext ctx) {
        return visit(ctx.primary());
    }

    @Override
    public ASTNode visitFuncCallExpression(MxstarParser.FuncCallExpressionContext ctx) {
        ExpressionNode function = (ExpressionNode) visit(ctx.expression());
        List<ExpressionNode> parameters = new ArrayList<>();
        if (ctx.parameterList() != null) {
            for (ParserRuleContext parameter : ctx.parameterList().expression()) {
                parameters.add((ExpressionNode) visit(parameter));
            }
        }
        return new FunctionCallExpressionNode(function, parameters, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitAssignExpression(MxstarParser.AssignExpressionContext ctx) {
        ExpressionNode leftChild = (ExpressionNode) visit(ctx.leftChild);
        ExpressionNode rightChild = (ExpressionNode) visit(ctx.rightChild);
        return new AssignExpressionNode(leftChild, rightChild, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitIdentifier(MxstarParser.IdentifierContext ctx) {
        return new IdentifierExpressionNode(ctx.Identifier().getText(), Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitThis(MxstarParser.ThisContext ctx) {
        return new ThisExpressionNode(Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitConstant(MxstarParser.ConstantContext ctx) {
        return visit(ctx.basicConstant());
    }

    @Override
    public ASTNode visitBody(MxstarParser.BodyContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public ASTNode visitInt(MxstarParser.IntContext ctx) {
        int value;
        try {
            value = Integer.parseInt(ctx.getText());
        } catch (Exception exception) {
            throw new SemanticError(Position.fromContext(ctx), "Invalid int constant: " + exception);
        }
        return new IntConstantExpressionNode(value, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitBool(MxstarParser.BoolContext ctx) {
        return new BoolConstantExpressionNode(ctx.getText().equals("true"), Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitSubscriptExpression(MxstarParser.SubscriptExpressionContext ctx) {
        return new SubscriptExpressionNode((ExpressionNode) visit(ctx.array), (ExpressionNode) visit(ctx.subscript), Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitNull(MxstarParser.NullContext ctx) {
        return new NullExpressionNode(Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitString(MxstarParser.StringContext ctx) {
        String string = ctx.getText().substring(1, ctx.getText().length() - 1);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            if (i + 1 < string.length() && string.charAt(i) == '\\') {
                switch (string.charAt(i + 1)) {
                    case '\\':
                        builder.append('\\');
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    case '\"':
                        builder.append('\"');
                        break;
                }
                ++i;
            } else {
                builder.append(string.charAt(i));
            }
        }
        return new StringConstantExpressionNode(builder.toString(), Position.fromContext(ctx));

    }

    @Override
    public ASTNode visitErrorCreator(MxstarParser.ErrorCreatorContext ctx) {
        throw new SemanticError(Position.fromContext(ctx), "Invalid creator");
    }

    @Override
    public ASTNode visitArrayCreator(MxstarParser.ArrayCreatorContext ctx) {
        TypeExpressionNode type = (TypeExpressionNode) visit(ctx.nonArrayType());
        List<ExpressionNode> dimensions = new ArrayList<>();
        for(ParserRuleContext dimension : ctx.expression()) {
            dimensions.add((ExpressionNode) visit(dimension));
        }
        int dimensionCount = (ctx.getChildCount() - 1 - dimensions.size()) >> 1;
        for (int i = 0; i < dimensionCount; ++i) {
            type.setType(new ArrayType(type.getType()));
        }
        return new NewExpressionNode(type, dimensions, dimensionCount, Position.fromContext(ctx));
    }

    @Override
    public ASTNode visitNonArrayCreator(MxstarParser.NonArrayCreatorContext ctx) {
        TypeExpressionNode type = (TypeExpressionNode) visit(ctx.nonArrayTypeCreator());
        return new NewExpressionNode(type, null, 0, Position.fromContext(ctx));
    }
}
