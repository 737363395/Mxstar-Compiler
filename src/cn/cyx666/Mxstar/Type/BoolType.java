package cn.cyx666.Mxstar.Type;

import cn.cyx666.Mxstar.Configuration.Configuration;

public class BoolType extends PrimitiveType {
    static private BoolType instance = new BoolType();

    private BoolType() {
        typeCategory = TypeCategory.BOOL;
        size = Configuration.getRegisterSize();
    }

    public static BoolType getInstance() {
        return instance;
    }
}
