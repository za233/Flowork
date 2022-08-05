package io.github.r1mao.ir.code;

import io.github.r1mao.DataType;

public class Value
{
    public static int LOCAL=0,TEMP=1,OPERATOR=2,CONSTANT=3,STACK=4;
    protected int valueType;
    protected DataType typeInCode;

    protected Value(int valueType)
    {
        this.valueType=valueType;
    }
    public int getValueType()
    {
        return this.valueType;
    }

    public String dump()
    {
        return "???";
    }
}
