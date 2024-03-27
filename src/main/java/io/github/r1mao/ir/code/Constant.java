package io.github.r1mao.ir.code;

import io.github.r1mao.DataType;

import java.util.ArrayList;

public class Constant extends Value {
    private final Object constant;
    private final DataType dataType;
    private final static Constant nullValue = new Constant(null);
    private static ArrayList<Constant> cache = new ArrayList<>();

    public Constant(Object constant) {
        super(Value.CONSTANT);
        this.constant = constant;
        if (constant == null)
            this.dataType = DataType.TYPE_REFERENCE;
        else
            this.dataType = DataType.getTypeByObject(this.constant);
        //System.out.println(DataType.getTypeName(this.dataType));
    }

    public static Value getConstant(Object o) {
        if (o == null) {
            if (!cache.contains(nullValue))
                cache.add(nullValue);
            return nullValue;
        }
        for (Constant c : cache)
            if (c.constant != null && c.constant.equals(o))
                return c;
        Constant c = new Constant(o);
        cache.add(c);
        return c;
    }

    public static ArrayList<Constant> getAllConstant() {
        return cache;
    }

    public String dump() {
        return "" + constant;
    }

}
