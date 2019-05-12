package cn.cyx666.Mxstar.Type;

abstract public class Type {
    public enum TypeCategory{
        VOID, INT, BOOL, STRING, CLASS, ARRAY, FUNCTION, NULL
    }

    int size;
    TypeCategory typeCategory;

    public int getSize() {
        return size;
    }
}
