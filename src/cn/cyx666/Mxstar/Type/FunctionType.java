package cn.cyx666.Mxstar.Type;

public class FunctionType extends Type {
    private String name;

    public FunctionType(String name) {
        typeCategory = TypeCategory.FUNCTION;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FunctionType)) return false;
        return name.equals(((FunctionType) object).name);
    }

    @Override
    public String toString() {
        return String.format("FunctionType(%s)", name);
    }
}
