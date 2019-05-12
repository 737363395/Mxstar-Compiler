package cn.cyx666.Mxstar.AST.DeclarationNode;

import cn.cyx666.Mxstar.AST.*;

import java.util.List;

public class ClassDeclarationNode extends DeclarationNode{
    private List<FunctionDeclarationNode> functions;
    private List<VariableDeclarationNode> variables;

    public ClassDeclarationNode(String name, List<FunctionDeclarationNode> functions, List<VariableDeclarationNode> variables, Position position){
        this.name = name;
        this.functions = functions;
        this.variables = variables;
        this.position = position;
    }

    public List<FunctionDeclarationNode> getFunctions() {
        return functions;
    }

    public List<VariableDeclarationNode> getVariables() {
        return variables;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
