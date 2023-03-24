/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.xproject.resizer.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseType<T> implements Serializable {

    @SuppressWarnings("rawtypes")
    private static final ConcurrentHashMap<Class, Map> enumValues = new ConcurrentHashMap<>();

    private final String value;

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected BaseType(String value) {
        this.value = value;

        enumValues.putIfAbsent(this.getClass(), new HashMap());

        enumValues.get(this.getClass()).put(value, this);
    }

    public String value() {
        return value;
    }

    public String toString() {
        return value;
    }

    public int hashCode() {
        return value.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this.getClass().isAssignableFrom(obj.getClass())) {
            return super.equals(obj);
        }

        return super.equals(valueOf(this.getClass(), obj.toString()));

    }

    @SuppressWarnings("unchecked")
    public static <T> T valueOf(Class<T> enumType, String name) {
        if (!enumValues.get(enumType).containsKey(name)) {
            throw new IllegalArgumentException("invalid value '" + name + "' for class " + enumType.getSimpleName());
        }

        return (T) enumValues.get(enumType).get(name);
    }
}
