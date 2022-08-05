package io.github.r1mao.ir.code;

import io.github.r1mao.ir.IRStatement;

public class StmtMonitor extends IRStatement
{
    private Value value;
    private boolean isEnter;
    public StmtMonitor(Value value,boolean isEnter)
    {
        this.value=value;
        this.isEnter=isEnter;
    }
    @Override
    public String dump()
    {
        if(this.isEnter)
            return "monitor_enter("+this.value.dump()+");";
        else
            return "monitor_exit("+this.value.dump()+");";
    }
}
