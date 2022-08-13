package io.github.r1mao.ir.code;

import io.github.r1mao.ir.IRStatement;

import java.util.ArrayList;

public class StmtThrow extends IRStatement
{
    private Value exception;
    public StmtThrow(Value exception)
    {
        this.exception=exception;
    }
    public Value getException()
    {
        return exception;
    }
    @Override
    public String dump()
    {
        return "throw "+this.exception.dump()+";";
    }
    @Override
    public ArrayList<Value> getReadVariable()
    {
        ArrayList<Value> arr=new ArrayList<>();
        arr.addAll(Value.getVariableValue(this.exception));
        return arr;
    }
    @Override
    public ArrayList<Value> getAllValues()
    {
        ArrayList<Value> a=new ArrayList<>();
        a.add(exception);
        return a;
    }
    @Override
    public void replaceUseOf(Value v1,Value v2)
    {
        if(!exception.isLeafValue() && v1!=exception)
        {
            Value.replaceUseOf(exception,v1,v2);
        }
        else
        {
            if(v1==exception)
                exception=v2;
        }
    }
}
