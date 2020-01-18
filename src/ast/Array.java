package ast;

import java.util.Arrays;

public class Array {

    private Object array[];
    private int size;

    public Array(int size){
        this.size = size;
        this.array = new Object[size];
    }

    public Object[] getArray() {
        return array;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }
}
