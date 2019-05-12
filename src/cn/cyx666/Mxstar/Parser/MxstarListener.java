// Generated from E:/Mxstar/src/cn/cyx666/Mxstar/Grammar\Mxstar.g4 by ANTLR 4.7.2
package cn.cyx666.Mxstar.Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MxstarParser}.
 */
public interface MxstarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MxstarParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MxstarParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MxstarParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(MxstarParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(MxstarParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(MxstarParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(MxstarParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(MxstarParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(MxstarParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(MxstarParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(MxstarParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#variableList}.
	 * @param ctx the parse tree
	 */
	void enterVariableList(MxstarParser.VariableListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#variableList}.
	 * @param ctx the parse tree
	 */
	void exitVariableList(MxstarParser.VariableListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(MxstarParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(MxstarParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMemberDeclaration(MxstarParser.MemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMemberDeclaration(MxstarParser.MemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#parameterDeclarationList}.
	 * @param ctx the parse tree
	 */
	void enterParameterDeclarationList(MxstarParser.ParameterDeclarationListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#parameterDeclarationList}.
	 * @param ctx the parse tree
	 */
	void exitParameterDeclarationList(MxstarParser.ParameterDeclarationListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#parameterDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterParameterDeclaration(MxstarParser.ParameterDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#parameterDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitParameterDeclaration(MxstarParser.ParameterDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#typeOrVoid}.
	 * @param ctx the parse tree
	 */
	void enterTypeOrVoid(MxstarParser.TypeOrVoidContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#typeOrVoid}.
	 * @param ctx the parse tree
	 */
	void exitTypeOrVoid(MxstarParser.TypeOrVoidContext ctx);
	/**
	 * Enter a parse tree produced by the {@code array}
	 * labeled alternative in {@link MxstarParser#type}.
	 * @param ctx the parse tree
	 */
	void enterArray(MxstarParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code array}
	 * labeled alternative in {@link MxstarParser#type}.
	 * @param ctx the parse tree
	 */
	void exitArray(MxstarParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nonArray}
	 * labeled alternative in {@link MxstarParser#type}.
	 * @param ctx the parse tree
	 */
	void enterNonArray(MxstarParser.NonArrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nonArray}
	 * labeled alternative in {@link MxstarParser#type}.
	 * @param ctx the parse tree
	 */
	void exitNonArray(MxstarParser.NonArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#nonArrayType}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayType(MxstarParser.NonArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#nonArrayType}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayType(MxstarParser.NonArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code block}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MxstarParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code block}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MxstarParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code condition}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterCondition(MxstarParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code condition}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitCondition(MxstarParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code loop}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterLoop(MxstarParser.LoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code loop}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitLoop(MxstarParser.LoopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code jump}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterJump(MxstarParser.JumpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code jump}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitJump(MxstarParser.JumpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code other}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterOther(MxstarParser.OtherContext ctx);
	/**
	 * Exit a parse tree produced by the {@code other}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitOther(MxstarParser.OtherContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blank}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlank(MxstarParser.BlankContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blank}
	 * labeled alternative in {@link MxstarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlank(MxstarParser.BlankContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(MxstarParser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(MxstarParser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#otherStatement}.
	 * @param ctx the parse tree
	 */
	void enterOtherStatement(MxstarParser.OtherStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#otherStatement}.
	 * @param ctx the parse tree
	 */
	void exitOtherStatement(MxstarParser.OtherStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nonVariableStatement}
	 * labeled alternative in {@link MxstarParser#localStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonVariableStatement(MxstarParser.NonVariableStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nonVariableStatement}
	 * labeled alternative in {@link MxstarParser#localStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonVariableStatement(MxstarParser.NonVariableStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code variableStatement}
	 * labeled alternative in {@link MxstarParser#localStatement}.
	 * @param ctx the parse tree
	 */
	void enterVariableStatement(MxstarParser.VariableStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code variableStatement}
	 * labeled alternative in {@link MxstarParser#localStatement}.
	 * @param ctx the parse tree
	 */
	void exitVariableStatement(MxstarParser.VariableStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#conditionStatement}.
	 * @param ctx the parse tree
	 */
	void enterConditionStatement(MxstarParser.ConditionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#conditionStatement}.
	 * @param ctx the parse tree
	 */
	void exitConditionStatement(MxstarParser.ConditionStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code while}
	 * labeled alternative in {@link MxstarParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhile(MxstarParser.WhileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code while}
	 * labeled alternative in {@link MxstarParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhile(MxstarParser.WhileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code for}
	 * labeled alternative in {@link MxstarParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void enterFor(MxstarParser.ForContext ctx);
	/**
	 * Exit a parse tree produced by the {@code for}
	 * labeled alternative in {@link MxstarParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void exitFor(MxstarParser.ForContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continue}
	 * labeled alternative in {@link MxstarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterContinue(MxstarParser.ContinueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continue}
	 * labeled alternative in {@link MxstarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitContinue(MxstarParser.ContinueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code break}
	 * labeled alternative in {@link MxstarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterBreak(MxstarParser.BreakContext ctx);
	/**
	 * Exit a parse tree produced by the {@code break}
	 * labeled alternative in {@link MxstarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitBreak(MxstarParser.BreakContext ctx);
	/**
	 * Enter a parse tree produced by the {@code return}
	 * labeled alternative in {@link MxstarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturn(MxstarParser.ReturnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code return}
	 * labeled alternative in {@link MxstarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturn(MxstarParser.ReturnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code primaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpression(MxstarParser.PrimaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code primaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpression(MxstarParser.PrimaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpression(MxstarParser.BinaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpression(MxstarParser.BinaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrefixExpression(MxstarParser.PrefixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrefixExpression(MxstarParser.PrefixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code subscriptExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSubscriptExpression(MxstarParser.SubscriptExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code subscriptExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSubscriptExpression(MxstarParser.SubscriptExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code suffixExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSuffixExpression(MxstarParser.SuffixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code suffixExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSuffixExpression(MxstarParser.SuffixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNewExpression(MxstarParser.NewExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNewExpression(MxstarParser.NewExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssignExpression(MxstarParser.AssignExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssignExpression(MxstarParser.AssignExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code memberAccessExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemberAccessExpression(MxstarParser.MemberAccessExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code memberAccessExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemberAccessExpression(MxstarParser.MemberAccessExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcCallExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFuncCallExpression(MxstarParser.FuncCallExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcCallExpression}
	 * labeled alternative in {@link MxstarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFuncCallExpression(MxstarParser.FuncCallExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code identifier}
	 * labeled alternative in {@link MxstarParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(MxstarParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code identifier}
	 * labeled alternative in {@link MxstarParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(MxstarParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code this}
	 * labeled alternative in {@link MxstarParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterThis(MxstarParser.ThisContext ctx);
	/**
	 * Exit a parse tree produced by the {@code this}
	 * labeled alternative in {@link MxstarParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitThis(MxstarParser.ThisContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constant}
	 * labeled alternative in {@link MxstarParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterConstant(MxstarParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constant}
	 * labeled alternative in {@link MxstarParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitConstant(MxstarParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by the {@code body}
	 * labeled alternative in {@link MxstarParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterBody(MxstarParser.BodyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code body}
	 * labeled alternative in {@link MxstarParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitBody(MxstarParser.BodyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code int}
	 * labeled alternative in {@link MxstarParser#basicConstant}.
	 * @param ctx the parse tree
	 */
	void enterInt(MxstarParser.IntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code int}
	 * labeled alternative in {@link MxstarParser#basicConstant}.
	 * @param ctx the parse tree
	 */
	void exitInt(MxstarParser.IntContext ctx);
	/**
	 * Enter a parse tree produced by the {@code string}
	 * labeled alternative in {@link MxstarParser#basicConstant}.
	 * @param ctx the parse tree
	 */
	void enterString(MxstarParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code string}
	 * labeled alternative in {@link MxstarParser#basicConstant}.
	 * @param ctx the parse tree
	 */
	void exitString(MxstarParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code null}
	 * labeled alternative in {@link MxstarParser#basicConstant}.
	 * @param ctx the parse tree
	 */
	void enterNull(MxstarParser.NullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code null}
	 * labeled alternative in {@link MxstarParser#basicConstant}.
	 * @param ctx the parse tree
	 */
	void exitNull(MxstarParser.NullContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bool}
	 * labeled alternative in {@link MxstarParser#basicConstant}.
	 * @param ctx the parse tree
	 */
	void enterBool(MxstarParser.BoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bool}
	 * labeled alternative in {@link MxstarParser#basicConstant}.
	 * @param ctx the parse tree
	 */
	void exitBool(MxstarParser.BoolContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#nonArrayTypeCreator}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayTypeCreator(MxstarParser.NonArrayTypeCreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#nonArrayTypeCreator}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayTypeCreator(MxstarParser.NonArrayTypeCreatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code errorCreator}
	 * labeled alternative in {@link MxstarParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterErrorCreator(MxstarParser.ErrorCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code errorCreator}
	 * labeled alternative in {@link MxstarParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitErrorCreator(MxstarParser.ErrorCreatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayCreator}
	 * labeled alternative in {@link MxstarParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreator(MxstarParser.ArrayCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayCreator}
	 * labeled alternative in {@link MxstarParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreator(MxstarParser.ArrayCreatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nonArrayCreator}
	 * labeled alternative in {@link MxstarParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayCreator(MxstarParser.NonArrayCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nonArrayCreator}
	 * labeled alternative in {@link MxstarParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayCreator(MxstarParser.NonArrayCreatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxstarParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(MxstarParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxstarParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(MxstarParser.ParameterListContext ctx);
}