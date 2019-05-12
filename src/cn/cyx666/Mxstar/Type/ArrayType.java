package cn.cyx666.Mxstar.Type;

import cn.cyx666.Mxstar.Configuration.Configuration;

public class ArrayType extends Type {
    private Type baseType;

    public ArrayType(Type baseType) {
        typeCategory = TypeCategory.ARRAY;
        this.baseType = baseType;
        size = Configuration.getRegisterSize();
    }

    public Type getBaseType() {
        return baseType;
    }

    @Override
    public boolean equals(Object boject) {
        if (!(boject instanceof ArrayType)) return false;
        return baseType.equals(((ArrayType) boject).baseType);
    }

    @Override
    public String toString() {
        return String.format("ArrayType(%s)", baseType.toString());
    }
}
