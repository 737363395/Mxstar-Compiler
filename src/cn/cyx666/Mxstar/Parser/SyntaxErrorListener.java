package cn.cyx666.Mxstar.Parser;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.Error.*;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SyntaxErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new SyntaxError(new Position(line, charPositionInLine), msg);
    }
}
