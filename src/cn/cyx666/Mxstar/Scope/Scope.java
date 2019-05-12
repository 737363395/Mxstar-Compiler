package cn.cyx666.Mxstar.Scope;

import cn.cyx666.Mxstar.AST.*;
import cn.cyx666.Mxstar.Error.*;
import cn.cyx666.Mxstar.Scope.Entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    static private final String KEY_PREFIX = "#";
    static private final String VARIABLE_PREFIX = "#VARIABLE#";
    static private final String CLASS_PREFIX = "#CLASS#";
    static private final String FUNCTION_PREFIX = "#FUNCTION#";
    static public final String ARRAY_CLASS_NAME = "class";
    static public final String STRING_CLASS_NAME = "string";
    static public final String THIS_PARAMETERS_NAME = "this";
    static private final int VARIABLE_PREFIX_LENGTH = VARIABLE_PREFIX.length();
    static private final int CLASS_PREFIX_LENGTH = CLASS_PREFIX.length();
    static private final int FUNCTION_PREFIX_LENGTH = FUNCTION_PREFIX.length();


    private Map<String, Entity> map = new HashMap<>();
    private Scope parent;
    private boolean isGlobal, isClass;

    public Scope() {
        this.isGlobal = true;
    }

    public Scope(Scope parent, boolean isClass) {
        this.parent = parent;
        this.isGlobal = false;
        this.isClass = isClass;
    }

    public Scope getParent() {
        return parent;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    static public String variableKey(String name) {
        return VARIABLE_PREFIX + name;
    }

    static public String classKey(String name) {
        return CLASS_PREFIX + name;
    }

    static public String functionKey(String name) {
        return FUNCTION_PREFIX + name;
    }

    public Entity get(String key) {
        Entity entity = map.get(key);
        return (isGlobal || entity != null) ? entity : parent.get(key);
    }

    public Entity getLocal(String key) {
        return map.get(key);
    }

    public Entity getCheck(String name, String key) {
        Entity entity = get(key);
        if (entity == null) throw new SemanticError("Entity " + name + "with key " + key + "is not found in the scope");
        return entity;
    }

    public Entity getCheck(Position position, String name, String key) {
        Entity entity = get(key);
        if (entity == null) throw new SemanticError(position, "Entity " + name + "with key " + key + "is not found in the scope");
        return entity;
    }

    public Entity getCheckLocal(String name, String key) {
        Entity entity = getLocal(key);
        if (entity == null) throw new SemanticError("Entity " + name + "with key " + key + "is not found in this scope");
        return entity;
    }

    public Entity getCheckNonClass(Position position, String name) {
        Entity entity = null;
        if (containsExactKeyLocal(variableKey(name))) return getLocal(variableKey(name));
        if (containsExactKeyLocal(functionKey(name))) return getLocal(functionKey(name));
        if (!isGlobal) entity = parent.getCheckNonClass(position, name);
        if (entity == null) throw new SemanticError(position, "Entity " + name + " is not found in the scope");
        return entity;
    }

    public Entity getCheckLocal(Position position, String name, String key) {
        Entity entity = getLocal(key);
        if (entity == null) throw new SemanticError(position, "Entity " + name + "with key " + key + "is not found in this scope");
        return entity;
    }

    public void assertContainsExactKey(Position position, String name, String key) {
        if (!containsExactKey(key)) throw new SemanticError(position, "Entity " + name + "with key " + key + "not found in scope");
    }

    public boolean containsExactKeyLocal(String key) {
        return map.containsKey(key);
    }

    public boolean containsExactKey(String key) {
        boolean bool = containsExactKeyLocal(key);
        return (isGlobal || bool) ? bool : parent.containsExactKey(key);
    }


    public boolean containsKeyLocal(String key) {
        String name;
        if (key.startsWith(VARIABLE_PREFIX)) {
            name = key.substring(VARIABLE_PREFIX_LENGTH);
            return map.containsKey(VARIABLE_PREFIX + name) || map.containsKey(FUNCTION_PREFIX + name);
        } else if (key.startsWith(CLASS_PREFIX)) {
            name = key.substring(CLASS_PREFIX_LENGTH);
            return map.containsKey(CLASS_PREFIX + name) || map.containsKey(FUNCTION_PREFIX + name);
        } else if (key.startsWith(FUNCTION_PREFIX)) {
            name = key.substring(FUNCTION_PREFIX_LENGTH);
            return map.containsKey(VARIABLE_PREFIX + name) || map.containsKey(CLASS_PREFIX + name) || map.containsKey(FUNCTION_PREFIX + name);
        } else return false;
    }

    public boolean containsKey(String key) {
        boolean bool = containsKeyLocal(key);
        return (isGlobal || bool) ? bool : parent.containsKey(key);
    }

    public boolean put(String key, Entity entity) {
        if (containsKeyLocal(key)) return false;
        map.put(key, entity);
        return true;
    }

    public void putCheck(String name, String key, Entity entity) {
        if (!put(key, entity)) throw new SemanticError("Symbol name \"" + name + "\" is already existed");
    }

    public void putCheck(Position position, String name, String key, Entity entity) {
        if (!put(key, entity)) throw new SemanticError(position, "Symbol name \"" + name + "\" is already existed");
    }
}
