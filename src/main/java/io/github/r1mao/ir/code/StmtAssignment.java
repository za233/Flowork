package io.github.r1mao.ir.code;

import io.github.r1mao.DataType;
import io.github.r1mao.ir.IRStatement;

public class StmtAssignment extends IRStatement
{
    private final Value to,value;
    public StmtAssignment(Value to, Value value)
    {
        this.to=to;
        this.value=value;
    }
    @Override
    public String dump()
    {
        if(to!=null)
            return to.dump()+" = "+value.dump()+";";
        else
            return value.dump()+";";
    }
}
