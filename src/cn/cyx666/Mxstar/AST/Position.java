package cn.cyx666.Mxstar.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Position {
    private final int row, column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Position(Token token) {
        this.row = token.getLine();
        this.column = token.getCharPositionInLine();
    }

    static public Position fromContext(ParserRuleContext ctx) {
        return new Position(ctx.getStart());
    }

    @Override
    public String toString() {
        return "(" + row + "," + column + ")";
    }
}