package io.github.r1mao.ir.code;

import io.github.r1mao.ir.IRStatement;

public class StmtThrow extends IRStatement
{
    private Value exception;
    public StmtThrow(Value exception)
    {
        this.exception=exception;
    }
    @Override
    public String dump()
    {
        return "throw "+this.exception.dump()+";";
    }
}
