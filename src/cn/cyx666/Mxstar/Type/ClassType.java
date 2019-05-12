package cn.cyx666.Mxstar.Type;

import cn.cyx666.Mxstar.Configuration.Configuration;

public class ClassType extends Type {
    private String name;

    public ClassType(String name) {
        typeCategory = TypeCategory.CLASS;
        this.name = name;
        size = Configuration.getRegisterSize();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClassType)) return false;
        return name.equals(((ClassType) object).name);
    }

    @Override
    public String toString() {
        return "ClassType(%" +  name + ")";
    }
}