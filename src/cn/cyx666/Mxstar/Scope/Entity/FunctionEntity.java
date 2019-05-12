package cn.cyx666.Mxstar.Scope.Entity;


import cn.cyx666.Mxstar.Scope.Scope;
import cn.cyx666.Mxstar.Type.*;
import cn.cyx666.Mxstar.AST.DeclarationNode.*;

import java.util.ArrayList;
import java.util.List;

public class FunctionEntity extends Entity{
    private List<VariableEntity> parameters;
    private Type returnType;
    private String className;
    private boolean isConstructor = false, isMember = false, isBuiltIn = false, externalImpact = false;

    public FunctionEntity(String name, Type type) {
        super(name, type);
    }

    public FunctionEntity(FunctionDeclarationNode node) {
        super(node.getName(), new FunctionType(node.getName()));
        parameters = new ArrayList<>();
        for (VariableDeclarationNode parameter : node.getParameters()) {
            parameters.add(new VariableEntity(parameter));
        }
        returnType = node.getReturnType() == null ? null : node.getReturnType().getType();
        isConstructor = node.isConstructor();
        className = null;
        isMember = false;
    }

    public FunctionEntity(FunctionDeclarationNode node, String className) {
        super(node.getName(), new FunctionType(node.getName()));
        parameters = new ArrayList<>();
        parameters.add(new VariableEntity(Scope.THIS_PARAMETERS_NAME, new ClassType(className)));
        for (VariableDeclarationNode parameter : node.getParameters()) {
            parameters.add(new VariableEntity(parameter));
        }
        returnType = node.getReturnType() == null ? null : node.getReturnType().getType();
        isConstructor = node.isConstructor();
        this.className = className;
        isMember = true;
    }

    public void setParameters(List<VariableEntity> parameters) {
        this.parameters = parameters;
    }

    public List<VariableEntity> getParameters() {
        return parameters;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public boolean isBuiltIn() {
        return isBuiltIn;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public boolean isExternalImpact() {
        return externalImpact;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setBuiltIn(boolean builtIn) {
        isBuiltIn = builtIn;
        if (builtIn) externalImpact = true;
    }

    public void setConstructor(boolean constructor) {
        isConstructor = constructor;
    }

    public void setExternalImpact(boolean externalImpact) {
        this.externalImpact = externalImpact;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public String getClassName() {
        return className;
    }
}
