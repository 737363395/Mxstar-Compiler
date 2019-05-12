package cn.cyx666.Mxstar.Type;

import cn.cyx666.Mxstar.Configuration.Configuration;

public class IntType extends PrimitiveType {
    static private IntType instance = new IntType();

    private IntType() {
        typeCategory = TypeCategory.INT;
        size = Configuration.getRegisterSize();
    }

    public static IntType getInstance() {
        return instance;
    }
}