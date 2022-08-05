package io.github.r1mao.ir.code;

import io.github.r1mao.DataType;

public class Constant extends Value
{
    private final Object constant;
    private final DataType dataType;
    public Constant(Object constant)
    {
        super(Value.CONSTANT);
        this.constant=constant;
        if(constant==null)
            this.dataType=DataType.TYPE_REFERENCE;
        else
            this.dataType=DataType.getTypeByObject(this.constant);
        //System.out.println(DataType.getTypeName(this.dataType));
    }
    public static Constant newConstant(Object value)
    {
        return new Constant(value);
    }

    public static Value getConstant(Object o)
    {
        return new Constant(o);
    }
    public String dump()
    {
        return ""+constant;
    }
}
