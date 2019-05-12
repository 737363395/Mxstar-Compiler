package cn.cyx666.Mxstar.Type;

import cn.cyx666.Mxstar.Configuration.Configuration;

public class StringType extends PrimitiveType {
    static private StringType instance = new StringType();

    private StringType() {
        typeCategory = TypeCategory.STRING;
        size = Configuration.getRegisterSize();
    }

    public static StringType getInstance() {
        return instance;
    }
}