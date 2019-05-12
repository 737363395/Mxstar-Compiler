package cn.cyx666.Mxstar.Error;

import cn.cyx666.Mxstar.AST.*;

public class SyntaxError extends Error {
    public SyntaxError(Position position, String message) {
        super("[Syntax Error] at " + position.toString() + " : " + message);
    }
}
