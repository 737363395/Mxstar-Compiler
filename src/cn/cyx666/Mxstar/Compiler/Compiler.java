package cn.cyx666.Mxstar.Compiler;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.AST.ProgramNode.*;
import cn.cyx666.Mxstar.IR.*;
import cn.cyx666.Mxstar.IR.IRBuilder;
import cn.cyx666.Mxstar.NASM.NASMPrinter;
import cn.cyx666.Mxstar.Parser.*;
import cn.cyx666.Mxstar.Scope.Scanner.*;
import cn.cyx666.Mxstar.Scope.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;

public class Compiler {
    private InputStream inputStream;
    private OutputStream outputStream;

    public Compiler(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void run() throws Exception {
        CharStream input = CharStreams.fromStream(inputStream);
        MxstarLexer lexer = new MxstarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MxstarParser parser = new MxstarParser(tokens);
        ParseTree tree;
        ASTBuilder astBuilder;
        ProgramNode ast;
        GlobalScopeScanner globalScopePreScanner;
        ClassScopeScanner classScopeScanner;
        FunctionScopeScanner functionScopeScanner;
        StaticUsageScopeScanner staticUsageScopeScanner;
        IRBuilder irBuilder;
        IR ir;
        NASMPrinter nasmPrinter;
        parser.removeErrorListeners();
        parser.addErrorListener(new SyntaxErrorListener());
        tree = parser.program();
        astBuilder = new ASTBuilder();
        ast = (ProgramNode) astBuilder.visit(tree);
        globalScopePreScanner = new GlobalScopeScanner();
        globalScopePreScanner.visit(ast);
        Scope globalScope = globalScopePreScanner.getScope();
        classScopeScanner = new ClassScopeScanner(globalScope);
        classScopeScanner.visit(ast);
        functionScopeScanner = new FunctionScopeScanner(globalScope);
        functionScopeScanner.visit(ast);
        staticUsageScopeScanner = new StaticUsageScopeScanner(globalScope);
        staticUsageScopeScanner.visit(ast);
        irBuilder = new IRBuilder(globalScope);
        irBuilder.visit(ast);
        ir = irBuilder.getIr();
        ir.toTwoOperands();
        ir.inlineFunction();
        ir.constantData();
        ir.parameterTransform();
        ir.livenessAnalysis();
        ir.registerAllocate();
        ir.toNASM();
        ir.simplifyInstruction();
        nasmPrinter = new NASMPrinter((PrintStream) outputStream);
        nasmPrinter.visit(ir);
    }
}
