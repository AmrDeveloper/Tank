package ast;

import java.util.Arrays;

public class Array {

    private final Object[] values;
    private final int length;

    public Array(int length){
        this.length = length;
        this.values = new Object[length];
    }

    public Array(Object[] values) {
        this.values = values;
        this.length = values.length;
    }

    public void setValue(Object value, int index){
        values[index] = value;
    }

    public Object getValue(int index){
        return values[index];
    }

    public Object[] getValues() {
        return values;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }
}
