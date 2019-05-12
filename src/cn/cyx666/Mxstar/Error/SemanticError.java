package cn.cyx666.Mxstar.Error;

import cn.cyx666.Mxstar.AST.*;

public class SemanticError extends Error{
    public SemanticError(Position position, String log) {
        super("Semantic Error at " + position.toString() + ": " + log);
    }
    public SemanticError(String log) {
        super("Semantic Error: " + log);
    }
}
