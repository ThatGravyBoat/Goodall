package tech.thatgravyboat.goodall.config;

import java.lang.reflect.Field;

public record PropertyData(Object fieldClass, Field field, AnnotationData data) {

    public String getId() {
        return field().getName();
    }

    public boolean getBoolean() throws IllegalAccessException {
        return field().getBoolean(fieldClass());
    }

    public void setBoolean(boolean _boolean) {
        try {
            field.setBoolean(fieldClass, _boolean);
        } catch (Exception e) {
            //Ignore
        }
    }

    public int getInt() throws IllegalAccessException {
        return field().getInt(fieldClass());
    }

    public void setInt(int _int) {
        try {
            field.setInt(fieldClass, _int);
        } catch (Exception e) {
            //Ignore
        }
    }

    public double getDouble() throws IllegalAccessException {
        return field().getDouble(fieldClass());
    }

    public void setDouble(double _double) {
        try {
            field.setDouble(fieldClass, _double);
        } catch (Exception e) {
            //Ignore
        }
    }
}
