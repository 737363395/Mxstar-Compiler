package cn.cyx666.Mxstar.Type;

public class NullType extends Type {
    static private NullType instance = new NullType();

    private NullType() {
        typeCategory = TypeCategory.NULL;
    }

    public static NullType getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "NullType";
    }
}
