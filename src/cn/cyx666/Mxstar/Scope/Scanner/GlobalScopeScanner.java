package cn.cyx666.Mxstar.Scope.Scanner;

import cn.cyx666.Mxstar.AST.DeclarationNode.*;
import cn.cyx666.Mxstar.AST.ProgramNode.*;
import cn.cyx666.Mxstar.Scope.Entity.*;
import cn.cyx666.Mxstar.Scope.*;
import cn.cyx666.Mxstar.Type.*;
import cn.cyx666.Mxstar.Error.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GlobalScopeScanner extends ScopeScanner {
    private Scope scope = new Scope();

    public Scope getScope() {
        return scope;
    }

    private void putBuiltInFunction(Scope scope, String name, List<VariableEntity> parameters, Type returnType) {
        FunctionEntity entity = new FunctionEntity(name, new FunctionType(name));
        entity.setParameters(parameters);
        entity.setBuiltIn(true);
        entity.setReturnType(returnType);
        if (!scope.isGlobal()) entity.setMember(true);
        scope.putCheck(name, Scope.functionKey(name), entity);
    }

    @Override
    public void visit(ProgramNode node) {
        String stringKey;
        ClassEntity stringEntity;
        String arrayKey;
        ClassEntity arrayEntity;
        FunctionEntity main;
        putBuiltInFunction(scope, "print", Collections.singletonList(new VariableEntity("str", StringType.getInstance())), VoidType.getInstance());
        putBuiltInFunction(scope, "println", Collections.singletonList(new VariableEntity("str", StringType.getInstance())), VoidType.getInstance());
        putBuiltInFunction(scope, "getString", new ArrayList<>(), StringType.getInstance());
        putBuiltInFunction(scope, "getInt", new ArrayList<>(), IntType.getInstance());
        putBuiltInFunction(scope, "toString", Collections.singletonList(new VariableEntity("i", IntType.getInstance())), StringType.getInstance());
        stringKey = Scope.classKey(Scope.STRING_CLASS_NAME);
        stringEntity = new ClassEntity(Scope.STRING_CLASS_NAME, new ClassType(Scope.STRING_CLASS_NAME), scope);
        putBuiltInFunction(stringEntity.getScope(), "length", Arrays.asList(new VariableEntity(Scope.THIS_PARAMETERS_NAME, StringType.getInstance())), IntType.getInstance());
        putBuiltInFunction(stringEntity.getScope(), "substring", Arrays.asList(new VariableEntity(Scope.THIS_PARAMETERS_NAME, StringType.getInstance()), new VariableEntity("left", IntType.getInstance()), new VariableEntity("right", IntType.getInstance())), StringType.getInstance());
        putBuiltInFunction(stringEntity.getScope(), "parseInt", Arrays.asList(new VariableEntity(Scope.THIS_PARAMETERS_NAME, StringType.getInstance())), IntType.getInstance());
        putBuiltInFunction(stringEntity.getScope(), "ord", Arrays.asList(new VariableEntity(Scope.THIS_PARAMETERS_NAME, StringType.getInstance()), new VariableEntity("pos", IntType.getInstance())), IntType.getInstance());
        scope.putCheck(Scope.STRING_CLASS_NAME, stringKey, stringEntity);
        arrayKey = Scope.classKey(Scope.ARRAY_CLASS_NAME);
        arrayEntity = new ClassEntity(Scope.ARRAY_CLASS_NAME, new ClassType(Scope.ARRAY_CLASS_NAME), scope);
        putBuiltInFunction(arrayEntity.getScope(), "size", Arrays.asList(new VariableEntity(Scope.THIS_PARAMETERS_NAME, new ArrayType(null))), IntType.getInstance());
        scope.putCheck(Scope.ARRAY_CLASS_NAME, arrayKey, arrayEntity);
        for (DeclarationNode declaration : node.getDeclarations()) {
            if (!(declaration instanceof VariableDeclarationNode)) declaration.accept(this);
        }
        main = (FunctionEntity) scope.get(Scope.functionKey("main"));
        if (main == null) throw new SemanticError("Invalid program without \"main\" function");
        if (!(main.getReturnType() instanceof IntType)) throw new SemanticError("Invalid return type for \"main\" function");
        if (!main.getParameters().isEmpty()) throw new SemanticError("Invalid parameter(s) for non-parameter \"main\" function");
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        String key = Scope.functionKey(node.getName());
        Entity entity = new FunctionEntity(node);
        scope.putCheck(node.position(), node.getName(), key, entity);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        String key = Scope.classKey(node.getName());
        Entity entity = new ClassEntity(node, scope);
        scope.putCheck(node.position(), node.getName(), key, entity);
    }
}
