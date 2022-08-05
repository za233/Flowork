package io.github.r1mao.ir.code;

import io.github.r1mao.DataType;
import io.github.r1mao.ir.IRStatement;

public class StmtReturn extends IRStatement
{
    private Value value;
    private DataType returnType;
    public StmtReturn(Value value, DataType type)
    {
        this.value=value;
        this.returnType=type;
    }
    @Override
    public String dump()
    {
        if(value!=null)
            return "return "+value.dump()+";";
        else
            return "return;";
    }
}
