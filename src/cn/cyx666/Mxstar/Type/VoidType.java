package cn.cyx666.Mxstar.Type;

public class VoidType extends PrimitiveType {
    static private VoidType instance = new VoidType();

    private VoidType() {
        typeCategory = TypeCategory.VOID;
    }

    public static VoidType getInstance() {
        return instance;
    }
}
