package cn.cyx666.Mxstar.AST.DeclarationNode;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.ExpressionNode.*;
import cn.cyx666.Mxstar.AST.StatementNode.*;

import java.util.List;

public class FunctionDeclarationNode extends DeclarationNode {
    private boolean isConstructor;
    private TypeExpressionNode returnType;
    private List<VariableDeclarationNode> parameters;
    private BlockStatementNode block;

    public FunctionDeclarationNode(TypeExpressionNode returnType, String name, List<VariableDeclarationNode> parameters, BlockStatementNode block, Position position){
        if (returnType == null) {
            this.isConstructor = true;
            this.returnType = null;
        } else {
            this.isConstructor = false;
            this.returnType = returnType;
        }
        this.name = name;
        this.parameters = parameters;
        this.position = position;
        this.block = block;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public TypeExpressionNode getReturnType() {
        return returnType;
    }

    public List<VariableDeclarationNode> getParameters() {
        return parameters;
    }

    public BlockStatementNode getBlock() {
        return block;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
